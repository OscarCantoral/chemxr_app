package com.example.ch3mxr.feature.learning

import android.content.Context
import androidx.compose.runtime.*
import com.example.ch3mxr.feature.experience.ChemDemoData
import com.example.ch3mxr.feature.main.model.DemoCourse
import com.example.ch3mxr.feature.selection.CourseSelectionCatalog
import org.json.JSONArray
import org.json.JSONObject
import java.net.URI
import java.time.LocalDateTime
import java.util.UUID

val LocalLearningStore = staticCompositionLocalOf<LearningStore> { error("LearningStore no inicializado") }

/** Estado observable y persistente del prototipo. Una sola cuenta demo, sin cobros ni servidor. */
class LearningStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("chemxr_learning_v1", Context.MODE_PRIVATE)
    private val restored = runCatching { JSONObject(prefs.getString("state", "{}") ?: "{}") }.getOrDefault(JSONObject())
    private var currentPlan by mutableStateOf(enumValue(restored.optString("plan"), StudentPlan.FREE))
    val plan: StudentPlan get() = currentPlan
    val isPro: Boolean get() = plan == StudentPlan.PRO
    val tutorAvailable: Boolean get() = isPro
    fun adsAllowed(spaceId: String) = spaceId == "personal" && !isPro

    val purchasedIds = mutableStateListOf<String>().apply { addAll(restored.strings("purchased").filter { CourseSelectionCatalog.find(it) != null }) }
    val enrolledIds = mutableStateListOf<String>().apply {
        addAll(if (restored.has("enrolled")) restored.strings("enrolled") else LearningCatalog.initialEnrollmentIds)
    }
    val cartIds = mutableStateListOf<String>().apply {
        addAll((if (restored.has("cart")) restored.strings("cart") else CourseSelectionCatalog.initialSelectionIds)
            .filter { CourseSelectionCatalog.find(it) != null && it !in purchasedIds }.distinct())
    }
    val resources = mutableStateListOf<CatalogResource>().apply {
        addAll(if (restored.has("resources")) restored.array("resources").objects().map { it.toResource() } else seedResources())
    }
    val shortcuts = mutableStateListOf<SavedShortcut>().apply {
        addAll(restored.array("shortcuts").objects().map { SavedShortcut(it.s("id"),it.s("space"),it.s("title"),it.s("kind"),it.s("route")) })
    }
    val posts = mutableStateListOf<CommunityPost>().apply {
        addAll(if (restored.has("posts")) restored.array("posts").objects().map { it.toPost() } else seedPosts())
    }
    val replies = mutableStateListOf<CommunityReply>().apply {
        addAll(if (restored.has("replies")) restored.array("replies").objects().map {
            CommunityReply(it.s("id"),it.s("post"),it.s("author"),it.s("initials"),it.s("text"),it.optInt("score"),it.s("resource"))
        } else seedReplies())
    }
    val votes = mutableStateMapOf<String, Int>().apply { putAll(restored.intMap("votes")) }
    val hidden = mutableStateListOf<String>().apply { addAll(restored.strings("hidden")) }
    val reports = mutableStateListOf<String>().apply { addAll(restored.strings("reports")) }
    private val roles = mutableStateMapOf<String, Int>().apply { putAll(restored.intMap("roles")) }
    val classEvents = mutableStateListOf<ClassroomEvent>().apply {
        addAll(restored.array("events").objects().map { ClassroomEvent(it.s("id"),it.s("course"),it.s("space"),it.s("section"),
            it.s("title"),it.s("type"),it.s("start"),it.s("end"),enumValue(it.s("role"),AcademicRole.STUDENT)) })
    }
    val classNotes = mutableStateListOf<ClassroomNote>().apply {
        addAll(restored.array("classNotes").objects().map { ClassroomNote(it.s("id"),it.s("course"),it.s("space"),it.s("section"),
            it.s("recording"),it.optInt("seconds"),it.s("text"),it.s("photo"),enumValue(it.s("role"),AcademicRole.STUDENT)) })
    }

    fun setPlan(value: StudentPlan) { currentPlan=value; persist() }
    fun canStudy(course: DemoCourse) = LearningRules.canStudy(course.access,plan,course.id in purchasedIds)
    fun addToCart(id: String) {
        if(CourseSelectionCatalog.find(id)!=null && id !in purchasedIds && id !in cartIds) {cartIds.add(id);persist()}
    }
    fun removeFromCart(id: String) {cartIds.remove(id);persist()}
    fun checkout(): Boolean {
        val valid=cartIds.filter {CourseSelectionCatalog.find(it)!=null && it !in purchasedIds}
        if(valid.isEmpty()) return false
        purchasedIds.addAll(valid);enrolledIds.addAll(valid.filter {it !in enrolledIds});cartIds.clear();persist();return true
    }
    fun enroll(course: DemoCourse): Boolean {
        if(!canStudy(course)) return false
        if(course.id !in enrolledIds) enrolledIds.add(course.id)
        persist();return true
    }
    fun myCourses(): List<DemoCourse> = LearningCatalog.all.filter {it.id in enrolledIds || it.id in purchasedIds}
    fun isSaved(id: String) = shortcuts.any {it.id==id}
    fun toggleShortcut(item: SavedShortcut) {
        if(isSaved(item.id)) shortcuts.removeAll {it.id==item.id} else shortcuts.add(0,item)
        persist()
    }
    fun vote(key: String, direction: Int) { votes[key]=LearningRules.nextVote(votes[key]?:0,direction);persist() }
    fun hide(key: String) {if(key !in hidden) hidden.add(key);persist()}
    fun showHidden() {hidden.clear();persist()}
    fun report(key: String, reason: String) {reports.add("$key · $reason");persist()}
    fun resource(id: String, spaceId: String) = resources.firstOrNull {it.id==id && it.spaceId==spaceId}

    fun registerResource(item: CatalogResource): Boolean {
        if(item.spaceId !in listOf("personal","utp","senati") || item.title.isBlank()) return false
        if(!isWebUrl(item.url) && !item.fileUri.startsWith("content://")) return false
        if(resources.any {it.id==item.id}) return false
        resources.add(0,item);persist();return true
    }
    fun linksRegistered(spaceId:String,text:String):Boolean = LearningRules.webLinks(text).all {url->
        resources.any {it.spaceId==spaceId && it.url.trimEnd('/')==url.trimEnd('/')}
    }
    fun publish(spaceId: String, text: String, resourceId: String=""): Boolean {
        if((text.isBlank() && resourceId.isBlank()) || !linksRegistered(spaceId,text)) return false
        if(resourceId.isNotBlank() && resource(resourceId,spaceId)==null) return false
        posts.add(0,CommunityPost(uid(),spaceId,"Oscar Cantoral","OC",text.trim(),resourceId=resourceId,
            group=if(spaceId=="personal") "Curiosos por la ciencia" else "Comunidad ${spaceId.uppercase()}"))
        persist();return true
    }
    fun reply(postId: String, text: String, resourceId:String=""): Boolean {
        val post=posts.firstOrNull {it.id==postId}?:return false
        if((text.isBlank() && resourceId.isBlank()) || !linksRegistered(post.spaceId,text)) return false
        if(resourceId.isNotBlank() && resource(resourceId,post.spaceId)==null) return false
        replies.add(CommunityReply(uid(),postId,"Oscar Cantoral","OC",text.trim(),resourceId=resourceId));persist();return true
    }
    fun ensureForum(course:ClassroomCourse):String {
        val id="forum_${course.id}"
        if(posts.none {it.id==id}) {
            posts.add(CommunityPost(id,course.spaceId,course.teacher,"DC",
                "Comparte tus preguntas y explica qué parte del curso necesitas reforzar.",group="Foro · ${course.title}"))
            replies.add(CommunityReply("${id}_welcome",id,"Delegado del salón","DS","Organicemos las preguntas y citemos nuestras fuentes.",2))
            persist()
        }
        return id
    }
    fun role(courseId: String): AcademicRole = AcademicRole.entries.getOrElse(roles[courseId]?:0) {AcademicRole.STUDENT}
    /** Selector EXCLUSIVO de demostración; una API real asignará el rol, jamás el cliente. */
    fun setDemoRole(courseId: String, value: AcademicRole) {if(ClassroomCatalog.find(courseId)!=null){roles[courseId]=value.ordinal;persist()}}
    fun addClassEvent(course: ClassroomCourse, title: String, type: String, start: LocalDateTime, end: LocalDateTime): Boolean {
        val granted=role(course.id)
        if(!LearningRules.canPublishClass(granted) || title.isBlank() || !end.isAfter(start)) return false
        if(ClassroomCatalog.find(course.id)!=course) return false
        classEvents.add(ClassroomEvent(uid(),course.id,course.spaceId,course.sectionId,title.trim(),type,start.toString(),end.toString(),granted))
        persist();return true
    }
    fun addClassNote(course: ClassroomCourse, recordingId: String, seconds: Int, text: String, photoUri: String): Boolean {
        val granted=role(course.id)
        if(!LearningRules.canPublishClass(granted) || seconds !in 0..3300 || (text.isBlank() && photoUri.isBlank())) return false
        if(ClassroomCatalog.find(course.id)!=course || recordingId!="${course.id}_session10") return false
        if(photoUri.isNotBlank() && !photoUri.startsWith("content://")) return false
        val id=uid()
        classNotes.add(0,ClassroomNote(id,course.id,course.spaceId,course.sectionId,recordingId,seconds,text.trim(),photoUri,granted))
        if(photoUri.isNotBlank()) resources.add(0,CatalogResource("photo_$id",course.spaceId,
            "Pizarra · ${course.title} · ${LearningRules.formatTimestamp(seconds)}",ResourceKind.IMAGE,course.title,
            "${granted.label} del salón",license="Material de clase",fileUri=photoUri,mime="image/*",
            origin="Grabación · Sesión 10",courseId=course.id))
        persist();return true
    }

    private fun persist() {
        val root=JSONObject().put("plan",plan.name).put("schema",1)
        root.put("purchased",JSONArray(purchasedIds.toList())).put("enrolled",JSONArray(enrolledIds.toList())).put("cart",JSONArray(cartIds.toList()))
        root.put("votes",JSONObject(votes.toMap())).put("roles",JSONObject(roles.toMap()))
            .put("hidden",JSONArray(hidden.toList())).put("reports",JSONArray(reports.toList()))
        root.put("resources",jsonArray(resources) {x->JSONObject().put("id",x.id).put("space",x.spaceId).put("title",x.title).put("kind",x.kind.name)
            .put("category",x.category).put("author",x.author).put("tags",x.tags).put("license",x.license).put("url",x.url).put("file",x.fileUri)
            .put("mime",x.mime).put("origin",x.origin).put("course",x.courseId)})
        root.put("shortcuts",jsonArray(shortcuts) {x->JSONObject().put("id",x.id).put("space",x.spaceId).put("title",x.title).put("kind",x.kind).put("route",x.route)})
        root.put("posts",jsonArray(posts) {x->JSONObject().put("id",x.id).put("space",x.spaceId).put("author",x.author).put("initials",x.initials)
            .put("text",x.text).put("score",x.baseScore).put("resource",x.resourceId).put("group",x.group)})
        root.put("replies",jsonArray(replies) {x->JSONObject().put("id",x.id).put("post",x.postId).put("author",x.author).put("initials",x.initials).put("text",x.text).put("score",x.baseScore).put("resource",x.resourceId)})
        root.put("events",jsonArray(classEvents) {x->JSONObject().put("id",x.id).put("course",x.courseId).put("space",x.spaceId).put("section",x.sectionId)
            .put("title",x.title).put("type",x.type).put("start",x.start).put("end",x.end).put("role",x.authorRole.name)})
        root.put("classNotes",jsonArray(classNotes) {x->JSONObject().put("id",x.id).put("course",x.courseId).put("space",x.spaceId).put("section",x.sectionId)
            .put("recording",x.recordingId).put("seconds",x.seconds).put("text",x.text).put("photo",x.photoUri).put("role",x.authorRole.name)})
        prefs.edit().putString("state",root.toString()).apply()
    }
}

fun isWebUrl(value: String): Boolean = runCatching {URI(value.trim()).let {it.scheme?.lowercase() in listOf("https","http") && !it.host.isNullOrBlank()}}.getOrDefault(false)
fun uid(): String = UUID.randomUUID().toString()
private inline fun <reified T:Enum<T>> enumValue(value:String,fallback:T):T = runCatching {enumValueOf<T>(value)}.getOrDefault(fallback)
private fun JSONObject.s(key:String)=optString(key,"")
private fun JSONObject.array(key:String)=optJSONArray(key)?:JSONArray()
private fun JSONObject.strings(key:String)=array(key).let {a->(0 until a.length()).map {a.optString(it)}}
private fun JSONArray.objects()=(0 until length()).mapNotNull {optJSONObject(it)}
private fun JSONObject.intMap(key:String):Map<String,Int> {val obj=optJSONObject(key)?:return emptyMap();return obj.keys().asSequence().associateWith {obj.optInt(it)}}
private fun <T> jsonArray(items:List<T>,write:(T)->JSONObject)=JSONArray().apply {items.forEach {put(write(it))}}
private fun JSONObject.toResource()=CatalogResource(s("id"),s("space"),s("title"),enumValue(s("kind"),ResourceKind.OTHER),s("category"),s("author"),s("tags"),s("license"),s("url"),s("file"),s("mime"),s("origin"),s("course"))
private fun JSONObject.toPost()=CommunityPost(s("id"),s("space"),s("author"),s("initials"),s("text"),optInt("score"),s("resource"),s("group"))
private fun seedResources():List<CatalogResource> = listOf("personal","utp","senati").flatMap {space->
    ChemDemoData.library.mapIndexed {i,x->CatalogResource("${space}_reference_$i",space,x.title,ResourceKind.LINK,x.category,
        "Fuente externa",tags=x.tags,url=x.url)}
}
private fun seedPosts()=listOf(
    CommunityPost("lucia","personal","Lucía Torres","LT","¿Qué sensores usarían para un robot que detecta obstáculos? Estoy preparando mi primer proyecto.",12,group="Curiosos por la ciencia"),
    CommunityPost("mateo","personal","Mateo Ramos","MR","Hoy hice mi primer modelo 3D de una molécula. ¿Qué recurso les ha servido para estudiar química?",8,group="Programamos juntos"),
    CommunityPost("utp_post1","utp","Lucía Torres","LT","¿Organizamos una sesión para resolver los ejercicios de Mecánica Clásica?",6,group="Comunidad UTP"),
    CommunityPost("utp_post2","utp","Mateo Ramos","MR","Comparto una referencia para preparar el informe de investigación.",4,"utp_reference_0","Comunidad UTP"),
    CommunityPost("senati_post1","senati","Ana Beltrán","AB","¿Qué práctica recomiendan para entender las redes locales?",5,group="Comunidad SENATI"),
    CommunityPost("senati_post2","senati","Diego Cruz","DC","Dejé una fuente de ingeniería en la biblioteca del espacio.",3,"senati_reference_2","Comunidad SENATI")
)
private fun seedReplies()=seedPosts().flatMap {post-> listOf(
    CommunityReply("${post.id}_c1",post.id,"Daniel Soto","DS",if(post.id=="lucia") "Empezaría con un sensor ultrasónico; mide distintas distancias y registra los resultados." else "Podemos revisar los ejemplos y comparar nuestros resultados.",4),
    CommunityReply("${post.id}_c2",post.id,"Ana Beltrán","AB","Me sirve organizar las ideas y citar la fuente de cada recurso.",2),
    CommunityReply("${post.id}_c3",post.id,"Carlos Medina","CM","¡Comparte tus avances para que podamos ayudarte!",1)
)}

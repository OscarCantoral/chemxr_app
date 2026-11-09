package com.example.ch3mxr.feature.auth.domain

import com.example.ch3mxr.core.model.*

/** Solo contratos. Sin SDK, sin tokens guardados ni OAuth activo todavia. */
enum class IdentityProvider {
  INSTITUTION,
  GOOGLE,
  FACEBOOK,
}

data class AuthenticatedUser(val id: String, val displayName: String? = null)

/** En el futuro se implementara con OIDC (identidad) + OAuth Authorization Code con PKCE. */
interface SessionRepository {
  suspend fun currentUser(): AuthenticatedUser?

  suspend fun signOut()
}

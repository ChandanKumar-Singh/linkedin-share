package electrovese.linkedin_share.session.utils

class Scope private constructor(vararg permissions: LIPermission?) {

    companion object {
        val R_LITEPROFILE = LIPermission("r_liteprofile", "Retrieve basic profile info: name, photo, headline")
        val R_EMAILADDRESS = LIPermission("r_emailaddress", "Retrieve email address")
        val UGC_POSTS = LIPermission("ugc_posts", "Post updates, comments, and likes as a user")

        @Synchronized
        fun build(vararg permissions: LIPermission): Scope {
            return Scope(*permissions)
        }
    }

    private val permissions: MutableSet<LIPermission> = HashSet()

    init {
        permissions.forEach { perm ->
            perm?.let { this.permissions.add(it) }
        }
    }

    fun createScope(): String {
        return join(" ", permissions)
    }

    private fun join(delimiter: String, tokens: Collection<LIPermission>): String {
        return tokens.joinToString(delimiter) { it.name }
    }

    override fun toString(): String {
        return createScope()
    }

    class LIPermission(val name: String, val description: String)
}

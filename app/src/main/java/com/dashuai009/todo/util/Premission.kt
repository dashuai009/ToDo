package com.dashuai009.todo.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat

class Permission private constructor() {
    companion object {
        private const val TAG = "PermissionUtils"
        fun hasPermissions(context: Context, vararg perms: String): Boolean {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.w(
                    TAG,
                    "PermissionUtils hasPermissions: API version < M, returning true by default"
                )
                // DANGER ZONE!!! Changing this will break the library.
                return true
            }
            requireNotNull(context) { "Can't check permissions for null context" }
            for (perm in perms){
                if (ContextCompat.checkSelfPermission(
                        context,
                        perm
                    ) !== PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
            return true
        }
    }

    init {
        throw UnsupportedOperationException("u can't instantiate me...")
    }
}

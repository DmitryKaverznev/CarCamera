package com.dkaverznev.carcamera.utils;

import com.dkaverznev.carcamera.R;

import java.util.Map;
import static java.util.Map.entry;

public class ErrorFirebaseUtils {

    private static final Map<String, Integer> ERROR_MESSAGES = Map.ofEntries(
            entry("ERROR_WEAK_PASSWORD", R.string.error_weak_password),
            entry("ERROR_INVALID_EMAIL", R.string.error_invalid_email),
            entry("ERROR_EMAIL_ALREADY_IN_USE", R.string.error_email_already_in_use),
            entry("ERROR_USER_NOT_FOUND", R.string.error_user_not_found),
            entry("ERROR_WRONG_PASSWORD", R.string.error_wrong_password),
            entry("ERROR_TOO_MANY_REQUESTS", R.string.error_too_many_requests),
            entry("ERROR_OPERATION_NOT_ALLOWED", R.string.error_operation_not_allowed),
            entry("ERROR_USER_DISABLED", R.string.error_user_disabled),
            entry("ERROR_CREDENTIAL_MISMATCH", R.string.error_user_mismatch),
            entry("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL", R.string.error_account_exists_with_different_credential),
            entry("ERROR_NETWORK_REQUEST_FAILED", R.string.error_network_request_failed)
    );

    public static int getString(String errorCode) {
        Integer resourceId = ERROR_MESSAGES.get(errorCode);
        return resourceId != null ? resourceId : R.string.error_unknown;
    }
}
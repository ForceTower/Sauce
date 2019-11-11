// ISyncServiceCallback.aidl
package dev.forcetower.core.service;

interface ISyncServiceCallback {
    void onLoginSuccessful(String name);
    void onLoginFailed(int reason, String message);
}

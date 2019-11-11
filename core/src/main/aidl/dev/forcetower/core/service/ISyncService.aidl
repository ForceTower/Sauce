// ISyncService.aidl
package dev.forcetower.core.service;

import dev.forcetower.core.service.ISyncServiceCallback;

interface ISyncService {
    void startSync();
    void stopSync();
    void registerCallback(ISyncServiceCallback callback);
    void unregisterCallback(ISyncServiceCallback callback);
}

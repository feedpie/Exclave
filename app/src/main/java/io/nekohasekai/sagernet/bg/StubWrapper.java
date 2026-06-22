package io.nekohasekai.sagernet.bg;

import io.nekohasekai.sagernet.aidl.AppStatsList;
import io.nekohasekai.sagernet.aidl.ISagerNetServiceCallback;
import io.nekohasekai.sagernet.aidl.TrafficStats;

public abstract class StubWrapper extends ISagerNetServiceCallback.Stub {

    @Override
    public void stateChanged(int state, String profileName, String msg) {}

    @Override
    public void trafficUpdated(long profileId, TrafficStats stats, boolean isCurrent) {}

    @Override
    public void statsUpdated(AppStatsList statsList) {}

    @Override
    public void observatoryResultsUpdated(long groupId) {}

    @Override
    public void profilePersisted(long profileId) {}

    @Override
    public void missingPlugin(String profileName, String pluginName) {}

    @Override
    public void routeAlert(int type, String routeName) {}
}

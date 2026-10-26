package org.apache.activemq.security.authorization.type;

import org.apache.activemq.security.authorization.SimpleCachedLDAPAuthorizationMap;

import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.ObjectChangeListener;

/**
 * Listener implementation for directory changes that maps change events to destination types.
 */
public class CachedLDAPAuthorizationMapNamespaceChangeListener implements NamespaceChangeListener, ObjectChangeListener {

    private final SimpleCachedLDAPAuthorizationMap simpleCachedLDAPAuthorizationMap;
    private final DestinationType destinationType;
    private final PermissionType permissionType;

    /**
     * Creates a new listener. If {@code permissionType} is {@code null}, add and remove events are ignored as they
     * do not directly affect policy state. This configuration is used when listening for changes on entries that
     * represent destination patterns and not for entries that represent permissions.
     *
     * @param destinationType the type of the destination being listened for
     * @param permissionType  the optional permission type being listened for
     */
    public CachedLDAPAuthorizationMapNamespaceChangeListener(SimpleCachedLDAPAuthorizationMap simpleCachedLDAPAuthorizationMap, DestinationType destinationType, PermissionType permissionType) {
        this.simpleCachedLDAPAuthorizationMap = simpleCachedLDAPAuthorizationMap;
        this.destinationType = destinationType;
        this.permissionType = permissionType;
    }

    @Override
    public void namingExceptionThrown(NamingExceptionEvent evt) {
        simpleCachedLDAPAuthorizationMap.namingExceptionThrown(evt);
    }

    @Override
    public void objectAdded(NamingEvent evt) {
        // This test is a hack to work around the fact that Apache DS 2.0 seems to trigger notifications
        // for the entire sub-tree even when one-level is the selected search scope.
        if (permissionType != null) {
            simpleCachedLDAPAuthorizationMap.objectAdded(evt, destinationType, permissionType);
        }
    }

    @Override
    public void objectRemoved(NamingEvent evt) {
        // This test is a hack to work around the fact that Apache DS 2.0 seems to trigger notifications
        // for the entire sub-tree even when one-level is the selected search scope.
        if (permissionType != null) {
            simpleCachedLDAPAuthorizationMap.objectRemoved(evt, destinationType, permissionType);
        }
    }

    @Override
    public void objectRenamed(NamingEvent evt) {
        simpleCachedLDAPAuthorizationMap.objectRenamed(evt, destinationType, permissionType);
    }

    @Override
    public void objectChanged(NamingEvent evt) {
        // This test is a hack to work around the fact that Apache DS 2.0 seems to trigger notifications
        // for the entire sub-tree even when one-level is the selected search scope.
        if (permissionType != null) {
            simpleCachedLDAPAuthorizationMap.objectChanged(evt, destinationType, permissionType);
        }
    }
}

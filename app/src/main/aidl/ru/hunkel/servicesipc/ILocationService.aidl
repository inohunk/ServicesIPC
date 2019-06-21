// ILocationService.aidl
package ru.hunkel.servicesipc;

interface ILocationService {
       void startTracking();
       void stopTracking();
       int getTrackingState();
}

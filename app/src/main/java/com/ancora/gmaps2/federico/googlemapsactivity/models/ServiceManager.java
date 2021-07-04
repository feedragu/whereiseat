package com.ancora.gmaps2.federico.googlemapsactivity.models;


import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;


public class ServiceManager
{
    private static final String tag = ServiceManager.class.getSimpleName();

    // Keep track of the service run state.
    private boolean serviceStarted = false;

    // The context from the application.
    private Context context;
    // The intent we will use to start the service and bind activities.
    private Intent helloServiceIntent = null;

    public ServiceManager(Context context)
    {
        this.context = context;

        // Create our intent.
        helloServiceIntent = new Intent(this.context, SocketService.class);
    }

    /**
     * Bind a Service Connection from a component (probably an Activity) to the
     * service so it can receive status updates.
     *
     * @param serviceConnection
     */
    public void bindService(ServiceConnection serviceConnection)
    {
        this.context.bindService(helloServiceIntent, serviceConnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * Determine if the service is started.
     * @return The run state of the service.
     */
    public boolean isServiceStarted()
    {
        return serviceStarted;
    }

    /**
     * Start the service.
     */
    public void startService()
    {
        serviceStarted = true;

        this.context.startService(helloServiceIntent);
    }

    /**
     * Stop the service.
     */
    public void stopService()
    {
        serviceStarted = false;

        this.context.stopService(helloServiceIntent);
    }


    /**
     * Unbind a Service Connection from a component (probably an Activity).
     * @param serviceConnection
     */
    public void unbindService(ServiceConnection serviceConnection)
    {
        this.context.unbindService(serviceConnection);
    }

    public boolean imAlive() {
        return true;
    }
}
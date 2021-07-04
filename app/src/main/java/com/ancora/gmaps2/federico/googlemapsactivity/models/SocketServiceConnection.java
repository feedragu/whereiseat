package com.ancora.gmaps2.federico.googlemapsactivity.models;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/*
 * SharedService
 * Copyright (C) 2014, Kaleb Kircher - Boki Software, Kircher Engineering, LLC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Interface for monitoring the state of an application service. In this case,
 * we use it to bind the activities to the service. The callback listener is set
 * from the owning activity and then forwarded to the service itself once the
 * activity has been connected.
 *
 * @author Kaleb
 *
 */
public class SocketServiceConnection implements ServiceConnection
{
    private static final String tag = SocketService.class
            .getSimpleName();

    // This is the object that receives interactions from clients.
    private IPostMonitor _service = null;

    // This is the object that receives interactions from service.
    private IPostListener _listener = null;

    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        Log.d(tag, "Service is connected.");
        _service = (IPostMonitor) service;
        _service.setListener(_listener);
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {
        _service = null;
        Log.d(tag, "Service is disconnected.");
    }

    /**
     * Set the callback listener that should be used to communicate with the
     * activity. This will be forwarded to the Service and *must* be called
     * before onBind() in this case.
     *
     * @param listener
     */
    public void setServiceListener(IPostListener listener)
    {
        _listener = listener;
    }
}
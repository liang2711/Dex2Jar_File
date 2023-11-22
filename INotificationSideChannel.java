// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package android.support.v4.app;

import android.app.Notification;
import android.os.IInterface;
import android.os.RemoteException;

public interface INotificationSideChannel
    extends IInterface
{
    /* member class not found */
    class Default {}

    /* member class not found */
    class Stub {}

    /* member class not found */
    class Stub.Proxy {}

    /* member class not found */
    class _Parcel {}


    public abstract void cancel(String s, int i, String s1)
        throws RemoteException;

    public abstract void cancelAll(String s)
        throws RemoteException;

    public abstract void notify(String s, int i, String s1, Notification notification)
        throws RemoteException;

    public static final String DESCRIPTOR = "android.support.v4.app.INotificationSideChannel";
}

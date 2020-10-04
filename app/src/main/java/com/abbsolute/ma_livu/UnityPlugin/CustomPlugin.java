package com.abbsolute.ma_livu.UnityPlugin;

import com.unity3d.player.UnityPlayer;

public final class CustomPlugin
{
    //Make class static variable so that the callback function is sent to one instance of this class.
    public static CustomPlugin testInstance;

    public static CustomPlugin instance()
    {
        if(testInstance == null)
        {
            testInstance = new CustomPlugin();
        }
        return testInstance;
    }

    String result = "";


    public String UnitySendMessageExtension(UnityPlayer mUnityPlayer,String gameObject, String functionName, String funcParam)
    {
        mUnityPlayer.UnitySendMessage(gameObject, functionName, funcParam);
        String tempResult = result;
        return tempResult;
    }

    //Receives result from C# and saves it to result  variable
    void receiveResult(String value)
    {
        result = "";//Clear old data
        result = value; //Get new one
    }
}
package org.godotengine.plugin.android.jank;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.UsedByGodot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main extends GodotPlugin{

    /**
     * Base constructor passing a {@link Godot} instance through which the plugin can access Godot's
     * APIs and lifecycle events.
     *
     * @param godot
     */
    public Main(Godot godot) {
        super(godot);
    }
    private Intent notificationIntent;
    private Activity context = getGodot().getActivity();
    private ImageParser imageParser = new ImageParser();

    @NonNull
    @Override
    public String getPluginName() {
        return "Main";
    }

    @UsedByGodot
    public void initializeIntent(){
        notificationIntent = new Intent(context, NotificationListener.class);
    }

    @UsedByGodot
    public void turnOnListener(){
        context.startService(notificationIntent);
    }

    @UsedByGodot
    public void turnOffListener(){
        context.stopService(notificationIntent);
    }

    @UsedByGodot
    public void ShareImage(ImageParser.GameInfo gameInfo){
        Bitmap bitmap = imageParser.encodePNG(gameInfo);
        try {
            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.myapp.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            context.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

}

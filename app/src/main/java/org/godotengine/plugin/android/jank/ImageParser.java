package org.godotengine.plugin.android.jank;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import androidx.core.content.FileProvider;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;

public class ImageParser{

    public int[] cyan = {0,255,255};
    public int[] magenta = {255,0,255};
    public int[] yellow = {255,255,0};
    public int[] black = {0,0,0};
    public int[][] vPixelOrder = { cyan, magenta, yellow, black, black, yellow, magenta, cyan };


    public GameInfo decodePNG(){
        File fileToParse = getTheNewestFile("Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images");
        GameInfo gameInfo = new GameInfo();
        String nameString = "";
        String moveMade = "";
        for(int i = 0; i < 25; i++){
            int[] pixelColor = getRGBatXYCoordinate(fileToParse, i, 0);
            if(i >= 8){
                if(i == 8){
                    gameInfo.setGameID(pixelColor[0]);
                    gameInfo.setPfp(pixelColor[1]);
                    gameInfo.setLocalGameID(pixelColor[2]);
                }
                if(i > 8 && i < 17){
                    for(int j = 0; j < 3; j++){
                        nameString += (char)pixelColor[j];
                    }
                }
                if(i >= 17){
                    for(int j = 0; j < 3; j++){
                        moveMade += (char)pixelColor[j];
                    }
                }
            }
            else{
                if(pixelColor != vPixelOrder[i]){
                    break;
                }
            }
        }
        gameInfo.setName(nameString);
        gameInfo.setMoveMade(moveMade);
        return gameInfo;
    }



    public Bitmap encodePNG(GameInfo gameInfo){
        File image = new File("images/invite.png");
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        int gameID = gameInfo.getGameID();
        int pfp = gameInfo.getPfp();
        int localGameID = gameInfo.getLocalGameID();
        char[] name = gameInfo.getName().toCharArray();
        int[][] nameAscii = new int[8][3];
        int k = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 3; j++){
                Array.set(nameAscii[i], j, (int)name[k]);
                k++;
            }
        }
        int[] idsAndPfp = {gameID, pfp, localGameID};
        char[] gameMove = gameInfo.getMoveMade().toCharArray();
        int[][] gameMoveAscii = new int[8][3];
        k = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 3; j++){
                Array.set(gameMoveAscii[i], j, (int)gameMove[k]);
                k++;
            }
        }
        for(int i = 0; i < 25; i++){
            if(i >= 8){
                if(i == 8){
                    bitmap = setRGBatXYCoordinate(idsAndPfp, bitmap, i, 0);
                }
                if(i > 8 && i < 17){
                    bitmap = setRGBatXYCoordinate(nameAscii[i], bitmap, i, 0);
                }
                if(i >= 17){
                    bitmap = setRGBatXYCoordinate(gameMoveAscii[i], bitmap, i, 0);
                }
            } else{
                bitmap = setRGBatXYCoordinate(vPixelOrder[i], bitmap, i, 0);
            }
        }
        return bitmap;
    }


    private Bitmap setRGBatXYCoordinate(int[] rgb, Bitmap bitmap, int x, int y){
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int color = Color.argb(0xFF, rgb[0], rgb[1], rgb[2]);
        mutableBitmap.setPixel(x, y, color);
        return mutableBitmap;
    }
    private int[] getRGBatXYCoordinate(File image, int x, int y){
        int[] returnRGB = new int[3];
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            if(bitmap != null){
                int pixelID = bitmap.getPixel(x, y);

                int r = Color.red(pixelID);
                int g = Color.green(pixelID);
                int b = Color.blue(pixelID);

                Array.set(returnRGB, 0, r);
                Array.set(returnRGB, 1, g);
                Array.set(returnRGB, 2, b);

            }
        }
        catch (Exception e){
            Array.set(returnRGB, 0, 404);
            Array.set(returnRGB, 0, 404);
            Array.set(returnRGB, 0, 404);
        }
        return returnRGB;
    }
    private File getTheNewestFile(String filePath) {
        File theNewestFile = null;
        File dir = new File(filePath);
        FileFilter fileFilter = new WildcardFileFilter("*.png");
        File[] files = dir.listFiles(fileFilter);

        if (files.length > 0) {
            /** The newest file comes first **/
            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            theNewestFile = files[0];
        }

        return theNewestFile;
    }
    public class GameInfo{
        public int gameID;
        public String name;
        public int pfp;
        public String moveMade;
        public int localGameID;

        public void setGameID(int id){
            gameID = id;
        }
        public void setName(String username){
            name = username;
        }
        public void setPfp(int pfpid){
            pfp = pfpid;
        }
        public void setMoveMade(String move){
            moveMade = move;
        }
        public void setLocalGameID(int id){ localGameID = id; }
        public int getGameID(){
            return gameID;
        }
        public String getName(){
            return name;
        }
        public int getPfp(){
            return pfp;
        }
        public String getMoveMade(){
            return moveMade;
        }
        public int getLocalGameID(){ return localGameID; }
    }

    public GameInfo basicValuesToGameInfo(int gameID, String name, int pfp, String moveMade, int localGameID){
        GameInfo gameInfo = new GameInfo();
        gameInfo.setGameID(gameID);
        gameInfo.setName(name);
        gameInfo.setPfp(pfp);
        gameInfo.setMoveMade(moveMade);
        gameInfo.setLocalGameID(localGameID);
        return gameInfo;
    }

    /*
    public JSONObject gameInfoToJSON(GameInfo gameInfo) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("GameID", gameInfo.getGameID());
        jsonObject.put("Name", gameInfo.getName());
        jsonObject.put("Pfp", gameInfo.getPfp());
        jsonObject.put("MoveMade", gameInfo.getMoveMade());
        jsonObject.put("LocalGameID", gameInfo.getLocalGameID());
        return jsonObject;
    }
    public void saveMostRecentGame(Context context) throws IOException, JSONException {
        // Convert JsonObject to String Format
        JSONObject jsonObject = gameInfoToJSON(decodePNG());
        String userString = jsonObject.toString();
// Define the File Path and its Name
        File file = new File(context.getFilesDir(), "incoming.json");
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(userString);
        bufferedWriter.close();
    }*/
}

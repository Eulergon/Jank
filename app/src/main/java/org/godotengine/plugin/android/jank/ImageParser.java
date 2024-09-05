package org.godotengine.plugin.android.jank;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import java.io.File;
import java.io.FileFilter;
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
                }
                if(i > 8 && i < 17){
                    for(int j = 0; j < 3; j++){
                        nameString += (char)pixelColor[j];
                    }
                }
                if(i > 17){
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


    /* Unfinished function
    public void encodePNG(GameInfo gameInfo){
        File image = new File("images/invite.png");
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
        int gameID = gameInfo.getGameID();
        int pfp = gameInfo.getPfp();
        char[] name = gameInfo.getName().toCharArray();
        int[] idAndPfp = {gameID, pfp};
        for(int i = 0; i < 25; i++){
            if(i >= 8){
                if(i == 8){
                    bitmap = setRGBatXYCoordinate(idAndPfp, bitmap, i, 0);
                }
                if(i > 8 && i < 17){
                    for(int j = 0; j < 3; j++){

                    }
                }
            } else{
                bitmap = setRGBatXYCoordinate(vPixelOrder[i], bitmap, i, 0);
            }
        }
    }

    */
    private Bitmap setRGBatXYCoordinate(int[] rgb, Bitmap bitmap, int x, int y){
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int color = Color.argb(0xFF, rgb[0], rgb[1], rgb[2]);
        mutableBitmap.setPixel(x, y, color);
        return mutableBitmap;
    }
    private int[] getRGBatXYCoordinate(File image, int x, int y){
        int[] returnRGB = new int[2];
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
    }

}
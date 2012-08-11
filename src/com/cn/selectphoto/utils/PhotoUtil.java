package com.cn.selectphoto.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class PhotoUtil
{
	protected Context instance;
	
	protected final String SDCARD_MNT = "/mnt/sdcard";
	protected final String SDCARD = "/sdcard";
	
	
	public PhotoUtil(Context context) {
		this.instance = context;
	}
	 /**
     * @param uri
     * @return
     */
	public String getAbsoluteImagePath(Uri uri) 
    {
		String imagePath = "";
        String [] proj={MediaStore.Images.Media.DATA};
        Cursor cursor = ((Activity)instance).managedQuery( uri,
                        proj, 		// Which columns to return
                        null,       // WHERE clause; which rows to return (all rows)
                        null,       // WHERE clause selection arguments (none)
                        null); 		// Order-by clause (ascending by name)
        
        if(cursor!=null)
        {
        	int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        	if(  cursor.getCount()>0 && cursor.moveToFirst() )
            {
            	imagePath = cursor.getString(column_index);
            }
        }
        
        return imagePath;
    }
	
	/**
	 * @param imgName
	 * @param kind   MediaStore.Images.Thumbnails.MICRO_KIND
	 * @return
	 */
	public Bitmap loadImgThumbnail( String imgName, int kind ) 
	{
		Bitmap bitmap = null;
		
        String[] proj = { MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME };
        
        Cursor cursor = ((Activity)instance).managedQuery(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, proj,
                        MediaStore.Images.Media.DISPLAY_NAME + "='" + imgName +"'", null, null);
       
        if ( cursor!=null && cursor.getCount()>0 && cursor.moveToFirst() ) 
        {
        	ContentResolver crThumb = ((Activity)instance).getContentResolver();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(crThumb,
            		cursor.getInt(0),
                	kind, options);
        } 
        return bitmap;
	}
	
	/**
	 * @return
	 */
	public String getLatestImage()
	{
		String latestImage = null;
		String[] items = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA }; 
		Cursor cursor = ((Activity)instance).managedQuery(
		                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
		                                items, 
		                                null,
		                                null, 
		                                MediaStore.Images.Media._ID + " desc");
		
		if( cursor != null && cursor.getCount()>0 )
		{
			cursor.moveToFirst();
			for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() )
			{
				latestImage = cursor.getString(1);
				break;
			}
		}
		
	    return latestImage;
	}
}
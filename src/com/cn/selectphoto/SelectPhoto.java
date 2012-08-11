package com.cn.selectphoto;

import java.io.File;

import com.cn.image.R;
import com.cn.selectphoto.utils.FileUtils;
import com.cn.selectphoto.utils.InfoHelper;
import com.cn.selectphoto.utils.MediaUtils;
import com.cn.selectphoto.utils.PhotoUtil;
import com.cn.selectphoto.utils.StringUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectPhoto extends Activity {
   
	private static final int REQUEST_CODE_GETIMAGE_BYSDCARD = 0;
	private static final int REQUEST_CODE_GETIMAGE_BYCAMERA = 1;
	private String thisLarge = null, theSmall = null;
	private ImageView imgView = null;
   private  PhotoUtil image;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btn = (Button)findViewById(R.id.bn03);
        imgView = (ImageView)findViewById(R.id.share_image);
        image = new PhotoUtil(this);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CharSequence[] items = {"手机相册", "手机拍照", "清除照片"};
				imageChooseItem(items);
			}
		});    
    }
    
    /**
	 * 操作选择
	 * @param items
	 */
	public void imageChooseItem(CharSequence[] items )
	{
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("增加图片").setItems(items,
			new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int item)
				{
					//手机选图
					if( item == 0 )
					{
						Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
						intent.setType("image/*"); 
						startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYSDCARD); 
					}
					//拍照
					else if( item == 1 )
					{	  
						Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");	
						
						String camerName = InfoHelper.getFileName();
						String fileName = "Share" + camerName + ".tmp";	
						
						File camerFile = new File( InfoHelper.getCamerPath(), fileName );
								
						theSmall = InfoHelper.getCamerPath() + fileName;
						thisLarge = image.getLatestImage();
						
						Uri originalUri = Uri.fromFile( camerFile );
					    intent.putExtra(MediaStore.EXTRA_OUTPUT, originalUri); 	
						startActivityForResult(intent, REQUEST_CODE_GETIMAGE_BYCAMERA);
					}   
					else if( item == 2 )
					{
						thisLarge = null;
						imgView.setBackgroundDrawable(null);
					}
				}}).create();
		
		 imageDialog.show();
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{ 
        if ( requestCode == REQUEST_CODE_GETIMAGE_BYSDCARD ) 
        { 
        	if (resultCode != RESULT_OK) 
    		{   
        		
    	        return;   
    	    }
        	
        	if(data == null)    return;
        	
        	Uri thisUri = data.getData();
        	String thePath = InfoHelper.getAbsolutePathFromNoStandardUri(thisUri);
        	
        	//如果是标准Uri
        	if( StringUtils.isBlank(thePath) )
        	{
        		thisLarge = image.getAbsoluteImagePath(thisUri);
        	}
        	else
        	{
        		thisLarge=thePath;
        	}
        	
        	String attFormat = FileUtils.getFileFormat(thisLarge);
        	if( !"photo".equals(MediaUtils.getContentType(attFormat)) )
        	{
        		Toast.makeText(this, "请选择图片文件！", Toast.LENGTH_SHORT).show();
        		return;
        	}
        	String imgName = FileUtils.getFileName(thisLarge);
    		
        	Bitmap bitmap = image.loadImgThumbnail(imgName, MediaStore.Images.Thumbnails.MICRO_KIND );
    		if(bitmap!=null)
    		{
    			imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
    		}
        }
        //拍摄图片
        else if(requestCode ==REQUEST_CODE_GETIMAGE_BYCAMERA )
        {	
        	if (resultCode != RESULT_OK) 
    		{   
    	        return;   
    	    }
        	
        	super.onActivityResult(requestCode, resultCode, data);
        	
        	Bitmap bitmap = InfoHelper.getScaleBitmap(this, theSmall);
        	
    		if(bitmap!=null)
    		{
    			imgView.setBackgroundDrawable(new BitmapDrawable(bitmap));
    		}
        }
        
        imgView.setOnClickListener( new OnClickListener(){
			@Override
			public void onClick(View v) {
    			Intent intent = new Intent();
				intent.setAction(android.content.Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(thisLarge)),"image/*");
				startActivity(intent);
			}
        });
    }
	
	
	
}
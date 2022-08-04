package com.example.maverickfilesender.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.maverickfilesender.R
import com.example.maverickfilesender.activities.MainActivity
import com.example.maverickfilesender.constants.Constants
import com.example.maverickfilesender.listeners.FileOnClickListener
import com.example.maverickfilesender.model.AppFile
import com.example.maverickfilesender.model.ParseFile
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.item_file.view.*
import java.io.ByteArrayOutputStream
import java.lang.Exception

class FilesRecyclerAdapter(val context: Context,val appFileList:ArrayList<AppFile>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mFileOnClickListener:FileOnClickListener?=null
val animation=AnimationUtils.loadAnimation(context,R.anim.bounce)
   val animationMoveUp=AnimationUtils.loadAnimation(context,R.anim.transition_up)
    var position=-1



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return FilesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_file,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

if(holder is FilesViewHolder){

Constants.filesIconView.add(holder.itemView.imv_fileIcon)


if(appFileList[position].onSelect!!){

    holder.itemView.imb_file_onSelect.background=ContextCompat.getDrawable(context,R.drawable.circle_selected)
    if(this.position!=-1 && position==this.position) {
        holder.itemView.imb_file_onSelect.startAnimation(animation)
        this.position=-1
    }
}
    else{
    holder.itemView.imb_file_onSelect.background=ContextCompat.getDrawable(context,R.drawable.circle_selector)
    }

    holder.itemView.imb_file_onSelect.setOnClickListener {
        val mainContext = context as MainActivity

        if (Constants.clientThread == null) {


            if (!appFileList[position]!!.file.isDirectory) {




            if (mainContext.ll_main_send.visibility == View.GONE) {

                mainContext.ll_main_send.visibility = View.VISIBLE
                mainContext.ll_main_send.startAnimation(animationMoveUp)
            }





            appFileList[position].onSelect = !appFileList[position].onSelect!!

            if (appFileList[position].onSelect!!) {

                var data: ByteArray? = null

//if(appFileList[position].drawable!=null){
                try {
                    val bitmap = getBitmapFromDrawable(holder.itemView.imv_fileIcon.drawable)
                    val stream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    data = stream.toByteArray()
                    appFileList[position].data = data
                } catch (e: Exception) {
                    appFileList[position].onSelect = !appFileList[position].onSelect!!
                    data = null
                    mainContext.ll_main_send.visibility = View.GONE
                }
                if (data != null) {
                    Constants.sendCount++

//}

                    Constants.tempSelectedFiles.add(ParseFile(appFileList[position].file, appFileList[position].data, "", null))

                    if (Constants.countList.isNotEmpty() && Constants.heirarchyFiles.isNotEmpty()) {
                        Constants.countList[Constants.countList.lastIndex] = Constants.countList[Constants.countList.lastIndex] + 1
                        Constants.heirarchyFiles[Constants.heirarchyFiles.lastIndex].add(ParseFile(appFileList[position].file, data, appFileList[position].file.path, null))
                    } else {
                        Constants.parentFiles.add(ParseFile(appFileList[position].file, data, "", null))
                    }
                }
            } else {
                Constants.sendCount--
                Constants.tempSelectedFiles.remove(ParseFile(appFileList[position].file, appFileList[position].data, "", null))

                if (Constants.countList.isNotEmpty() && Constants.heirarchyFiles.isNotEmpty()) {
                    Constants.countList[Constants.countList.lastIndex] = Constants.countList[Constants.countList.lastIndex] - 1
                    Constants.heirarchyFiles[Constants.heirarchyFiles.lastIndex].remove(ParseFile(appFileList[position].file, appFileList[position].data, "", null))
                } else {
                    Constants.parentFiles.remove(ParseFile(appFileList[position].file, appFileList[position].data, "", null))
                }

                if (Constants.tempSelectedFiles.isEmpty()) {
                    (context as MainActivity).ll_main_send.startAnimation((context as MainActivity).transitionDown)
                    (context as MainActivity).ll_main_send.visibility = View.GONE
                }

            }

            this.notifyItemChanged(position)


        }
            else{
                mainContext.showErrorMessage("Can't send directories.")
            }
    }


    }


    holder.itemView.tv_item_fileName.text = appFileList[position].file.name
    if(appFileList[position].file.isDirectory) {

    holder.itemView.imv_fileIcon.setImageResource(R.drawable.folder_icon)

        holder.itemView.ll_itemFile.setOnClickListener {

mFileOnClickListener!!.onClick(appFileList[position].file)



        }

}
 else{

     if(appFileList[position].file.name.endsWith(".PNG")||
             appFileList[position].file.name.endsWith(".jpg")||
             appFileList[position].file.name.endsWith(".mp4")

     ){

         Glide.with(context)
                 .load(appFileList[position].file)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)





     }

        else if(appFileList[position].file.name.endsWith(".avi")||
             appFileList[position].file.name.endsWith(".mkv")){
         Glide.with(context)
                 .load(R.drawable.video_icon)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)


     }

        else if(appFileList[position].file.name.endsWith(".pdf")||
             appFileList[position].file.name.endsWith(".PDF")){

            Glide.with(context)
                    .load(R.drawable.pdf_icon)
                    .centerCrop()
                    .into(holder.itemView.imv_fileIcon)





     }

     else if(appFileList[position].file.name.endsWith(".zip")||
             appFileList[position].file.name.endsWith(".rar")||
             appFileList[position].file.name.endsWith(".xz")){

         Glide.with(context)
                 .load(R.drawable.zip_icon)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)


     }


     else if(appFileList[position].file.name.endsWith(".docx")){

         Glide.with(context)
                 .load(R.drawable.docx_icon)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)



     }
     else if(appFileList[position].file.name.endsWith(".pptx")){

         Glide.with(context)
                 .load(R.drawable.ppt_icon)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)


     }

     else if(appFileList[position].file.name.endsWith(".xlsx")){

         Glide.with(context)
                 .load(R.drawable.xlsx_icon)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)



     }


       else if(appFileList[position].file.name.endsWith(".apk")){


            Glide.with(context)
                    .load(appFileList[position].drawable)
                    .centerCrop()
                    .into(holder.itemView.imv_fileIcon)




        }
        else if(Constants.isDarkMode){


         Glide.with(context)
                 .load(R.drawable.ic_outline_image_24)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)



     }
        else{
         Glide.with(context)
                 .load(R.drawable.ic_outline_image_24)
                 .centerCrop()
                 .into(holder.itemView.imv_fileIcon)


     }


}


}
    }



    fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val bitmap= Bitmap.createBitmap(drawable.intrinsicWidth,drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas= Canvas(bitmap)
        drawable.setBounds(0,0,canvas.width,canvas.height)
        drawable.draw(canvas)
        return bitmap

    }

    fun setOnClickListener( mFilesOnClickListener: FileOnClickListener){
        this.mFileOnClickListener=mFilesOnClickListener
    }

    override fun getItemCount(): Int {
        return appFileList.size
    }

    class FilesViewHolder(val view: View):RecyclerView.ViewHolder(view)

}
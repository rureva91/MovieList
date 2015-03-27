// AddEditFragment.java
// Allows user to add a new movie or edit an existing one
package com.example.MovieList;

import com.example.MovieList.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class AddEditFragment extends Fragment
{
   // callback method implemented by MainActivity  
   public interface AddEditFragmentListener
   {
      // called after edit completed so movie can be redisplayed
      public void onAddEditCompleted(long rowID);
   }
   
   private AddEditFragmentListener listener; 
   
   private long rowID; // database row ID of the movie
   private Bundle movieInfoBundle; // arguments for editing a movie

   // EditTexts for movie information
   private EditText nameEditText;
   private EditText directorEditText;
   private EditText musicEditText;
   private EditText languageEditText;
   private EditText releasedateEditText;
   private EditText actressEditText;
   private EditText actorEditText;

   // set AddEditFragmentListener when Fragment attached   
   @Override
   public void onAttach(Activity activity)
   {
      super.onAttach(activity);
      listener = (AddEditFragmentListener) activity; 
   }

   // remove AddEditFragmentListener when Fragment detached
   @Override
   public void onDetach()
   {
      super.onDetach();
      listener = null; 
   }
   
   // called when Fragment's view needs to be created
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState)
   {
      super.onCreateView(inflater, container, savedInstanceState);    
      setRetainInstance(true); // save fragment across config changes
      setHasOptionsMenu(true); // fragment has menu items to display
      
      // inflate GUI and get references to EditTexts
      View view = 
         inflater.inflate(R.layout.fragment_add_edit, container, false);
     nameEditText = (EditText) view.findViewById(R.id.nameEditText);
      directorEditText = (EditText) view.findViewById(R.id.directorEditText);
     musicEditText = (EditText) view.findViewById(R.id.musicEditText);
     languageEditText = (EditText) view.findViewById(R.id.languageEditText);
     releasedateEditText = (EditText) view.findViewById(R.id.releasedateEditText);
     actressEditText = (EditText) view.findViewById(R.id.actressEditText);
      actorEditText = (EditText) view.findViewById(R.id.actorEditText);

      movieInfoBundle = getArguments(); // null if creating new movie

      if (movieInfoBundle != null)
      {
         rowID = movieInfoBundle.getLong(MainActivity.ROW_ID);
    nameEditText.setText(movieInfoBundle.getString("name"));  
   directorEditText.setText(movieInfoBundle.getString("director"));  
        musicEditText.setText(movieInfoBundle.getString("music"));  
        languageEditText.setText(movieInfoBundle.getString("language"));  
        releasedateEditText.setText(movieInfoBundle.getString("releasedate"));  
        actressEditText.setText(movieInfoBundle.getString("actress"));  
         actorEditText.setText(movieInfoBundle.getString("actor"));  
      } 
      
      // set Save movie Button's event listener 
      Button saveContactButton = 
         (Button) view.findViewById(R.id.saveContactButton);
      saveContactButton.setOnClickListener(saveButtonClicked);
      return view;
   }

   // responds to event generated when user saves a movie
   OnClickListener saveButtonClicked = new OnClickListener() 
   {
      @Override
      public void onClick(View v) 
      {
         if (nameEditText.getText().toString().trim().length() != 0)
         {
            // AsyncTask to save movie, then notify listener 
            AsyncTask<Object, Object, Object> saveContactTask = 
               new AsyncTask<Object, Object, Object>() 
               {
                  @Override
                  protected Object doInBackground(Object... params) 
                  {
                     save(); // save movie to the database
                     return null;
                  } 
      
                  @Override
                  protected void onPostExecute(Object result) 
                  {
                     // hide soft keyboard
                     InputMethodManager imm = (InputMethodManager) 
                        getActivity().getSystemService(
                           Context.INPUT_METHOD_SERVICE);
                     imm.hideSoftInputFromWindow(
                        getView().getWindowToken(), 0);

                     listener.onAddEditCompleted(rowID);
                  } 
               }; // end AsyncTask
               
            // save the movie to the database using a separate thread
            saveContactTask.execute((Object[]) null); 
         } 
         else // required movie name is blank, so display error dialog
         {
            DialogFragment errorSaving = 
               new DialogFragment()
               {
                  @Override
                  public Dialog onCreateDialog(Bundle savedInstanceState)
                  {
                     AlertDialog.Builder builder = 
                        new AlertDialog.Builder(getActivity());
                     builder.setMessage(R.string.error_message);
                     builder.setPositiveButton(R.string.ok, null);                     
                     return builder.create();
                  }               
               };
            
            errorSaving.show(getFragmentManager(), "error saving movie");
         } 
      } // end method onClick
   }; // end OnClickListener savemovieButtonClicked

   // saves movie information to the database
   private void save() 
   {
      // get DatabaseConnector to interact with the SQLite database
      DatabaseConnector databaseConnector = 
         new DatabaseConnector(getActivity());

      if (movieInfoBundle == null)
      {
         // insert the movie information into the database
         rowID = databaseConnector.insertMovie(
        		 nameEditText.getText().toString(),
        		 directorEditText.getText().toString(), 
        		 musicEditText.getText().toString(), 
        		 languageEditText.getText().toString(),
        		 releasedateEditText.getText().toString(), 
        		 actressEditText.getText().toString(), 
        		 actorEditText.getText().toString());
      } 
      else
      {
         databaseConnector.updateMovie(rowID,
        	nameEditText.getText().toString(),
            directorEditText.getText().toString(), 
            musicEditText.getText().toString(), 
            languageEditText.getText().toString(),
            releasedateEditText.getText().toString(), 
            actressEditText.getText().toString(), 
            actorEditText.getText().toString());
      }
   } // end method savemovie
} // end class AddEditFragment




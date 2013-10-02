package com.example.scrolllistviewdemo;

import android.R.string;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	/** Called when the activity is first created. */
	ListView list;
	private List<String> List_file;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		List_file = new ArrayList<String>();
		list = (ListView) findViewById(R.id.listview);

		CreateListView();
	}

	private void CreateListView() {
		List_file.add("Coderzheaven");
		List_file.add("Google");
		List_file.add("Android");
		List_file.add("iPhone");
		List_file.add("Apple");
		List_file.add("Apple2");
		List_file.add("Apple3");
		List_file.add("Apple4");
		List_file.add("Apple5");
		List_file.add("Apple6");
		List_file.add("Apple7");
		List_file.add("Apple8");
		List_file.add("Apple9");
		List_file.add("Apple10");
		// Create an adapter for the listView and add the ArrayList to the
		// adapter.
		list.setAdapter(new ArrayAdapter<String>(MainActivity.this,
				android.R.layout.simple_list_item_1, List_file));
//		list.setOnItemClickListener(new OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// args2 is the listViews Selected index
//			}
//		});
	}
}

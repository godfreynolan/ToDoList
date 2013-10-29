package com.logicdrop.todos;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.os.StrictMode;

public class TodoActivity extends Activity
{
	public static final String APP_TAG = "com.logicdrop.todos";

	private ListView taskView;
	private Button btNewTask;
	private EditText etNewTask;
	private TodoProvider provider;

	// TIP: Use static/final where appropriate
	private OnClickListener handleNewTaskEvent = new OnClickListener()
	{
		@Override
		public void onClick(final View view)
		{
			Log.d(APP_TAG, "add task click received");

			TodoActivity.this.provider.addTask(TodoActivity.this
					.getEditText()
					.getText()
					.toString());

			TodoActivity.this.renderTodos();
		}
	};

    // TIP: Traceview
    // SEE: deoptimizedTraceview.png, optimizedTraceview.png, deoptimizedDDMS.trace, optimizedDDMS.trace
	@Override
	protected void onStop()
	{
		super.onStop();

		// Debug.stopMethodTracing();
	}

	@Override
	protected void onStart()
	{
        // Debug.startMethodTracing("ToDo");

		super.onStart();
	}

	// TIP: Use floats judiciously
    // SEE: float-vs-integers.tiff
	@SuppressWarnings("unused")
	private void showFloatVsIntegerDifference()
	{
		int max = 1000;
		float f = 0;
		int i = 0;
		long startTime, elapsedTime;

		// Compute time for floats
		startTime = System.nanoTime();
		for (float x = 0; x < max; x++)
		{
			f += x;
		}
		elapsedTime = System.nanoTime() - startTime;
		Log.v(APP_TAG, "Floating Point Loop: " + elapsedTime);

		// Compute time for ints
		startTime = System.nanoTime();
		for (int x = 0; x < max; x++)
		{
			i += x;
		}
		elapsedTime = System.nanoTime() - startTime;
		Log.v(APP_TAG, "Integer Point Loop: " + elapsedTime);
	}

	// TIP: Avoid creating unnecessary objects or memory allocation
    // SEE: allocations-before.tiff, heap-before.tiff
	private void createPlaceholders()
	{
		this.getProvider().deleteAll();

		if (this.getProvider().findAll().isEmpty())
		{
            // TIP: Arrays are faster than vectors
			List<String> beans = new ArrayList<String>();

			// TIP: Use enhanced for loop
			for (int i = 0; i < 10; i++)
			{
				String title = "Placeholder " + i;
				this.getProvider().addTask(title);
				beans.add(title);
			}
		}
	}

	EditText getEditText()
	{
		return this.etNewTask;
	}

	// TIP: Avoid private getters/setters - consider using package
	private TodoProvider getProvider()
	{
		return this.provider;
	}

	private ListView getTaskView()
	{
		return this.taskView;
	}

	public void onCreate(final Bundle bundle)
	{
        // TIP: Use Strictmode to detect unwanted disk or network access
        // Remove from production code

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
		super.onCreate(bundle);

        // TIP: Do not overuse Linearlayouts, as they become more complex
        // Replace them with Relativelayouts, increasing UI loading speed
		this.setContentView(R.layout.main);

		this.provider = new TodoProvider(this);
		this.taskView = (ListView) this.findViewById(R.id.tasklist);
		this.btNewTask = (Button) this.findViewById(R.id.btNewTask);
		this.etNewTask = (EditText) this.findViewById(R.id.etNewTask);
		this.btNewTask.setOnClickListener(this.handleNewTaskEvent);

		this.showFloatVsIntegerDifference();

		this.createPlaceholders();

		this.renderTodos();

        // TIP: Again, don't allocate unnecessary objects that expands the heap size to significant proportions
        // Once GC occurs, a large amount of the heap memory is dumped, especially with
        // local data structures, render a large portion of the heap unused.
        // SEE: deoptimizedHeap.png, optimizedHeap.png

        // the following code will crash the app if uncommented
        // ArrayList<uselessClass> uselessObject = new ArrayList<uselessClass>();
        // for (int i=0; i<100000; i++)
        //    uselessObject.add(new uselessClass());
	}

	private void renderTodos()
	{
		List<String> beans = this.getProvider().findAll();

		Log.d(APP_TAG, String.format("%d beans found", beans.size()));

		this.getTaskView().setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, beans
								.toArray(new String[]
								{})));

		this.getTaskView().setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(final AdapterView<?> parent,
					final View view, final int position, final long id)
			{
				Log.d(APP_TAG, String.format(
						"item with id: %d and position: %d", id, position));

				TextView v = (TextView) view;
				TodoActivity.this.getProvider().deleteTask(
						v.getText().toString());
				TodoActivity.this.renderTodos();
			}
		});
	}

    // Class with 26 double data members used to expand heap size in example
    private class uselessClass {
        @SuppressWarnings("unused")
		double a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z;
    }
}
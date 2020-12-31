package com.example.todolist;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    private RecyclerViewAdapterTask recyclerViewAdapterTask;
    private RecyclerView recyclerViewTask;

    private EditText itemTaskName;
    private List<Task> taskList;
    private Button btn_saveTask;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_task);

        TextView detslistname = findViewById(R.id.dets_listName);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String listName = bundle.getString("ListName");

            detslistname.setText(listName + " List");
        }

        recyclerViewTask = findViewById(R.id.recyclerViewTask);
        recyclerViewTask.setHasFixedSize(true);
        recyclerViewTask.setLayoutManager(new LinearLayoutManager(this));

        taskList = new ArrayList<>();

        recyclerViewAdapterTask = new RecyclerViewAdapterTask(this, taskList);
        recyclerViewTask.setAdapter(recyclerViewAdapterTask);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewTask);

        Button btn_createTask = findViewById(R.id.btn_createTask);
        btn_createTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPopDialog();
            }
        });

        TextView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



    private void createPopDialog() {
        builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.list_name, null);
        itemTaskName = view.findViewById(R.id.edit_text);
        btn_saveTask = view.findViewById(R.id.submit_list);

        TextView title = view.findViewById(R.id.typing_name);
        title.setText("Enter Task Name : ");
        itemTaskName.setHint("Task Name");

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        btn_saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!itemTaskName.getText().toString().isEmpty()) {
                    saveItem(v);
                } else {
                    Snackbar.make(v, "Empty Field not Allowed!", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveItem(View v) {

        String newItemNOT = itemTaskName.getText().toString().trim();

        Task task = new Task(newItemNOT, false);
        taskList.add(0, task);
        recyclerViewAdapterTask.notifyItemInserted(0);
        recyclerViewTask.smoothScrollToPosition(0);

        alertDialog.dismiss();
    }

    public void onCheckboxClicked(View view) {
    }

    Task NEWITEM = null;
    Task POPITEM = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(taskList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    POPITEM= taskList.get(position);
                    taskList.remove(POPITEM);
                    recyclerViewAdapterTask.notifyItemRemoved(position);
                    String nameTask = POPITEM.getTaskName();
                    Snackbar.make(recyclerViewTask, nameTask + " DELETED", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    taskList.add(position, POPITEM);
                                    recyclerViewAdapterTask.notifyItemInserted(position);
                                }
                            }).show();
                    break;

                case ItemTouchHelper.RIGHT:
                    NEWITEM = taskList.get(position);

                    builder = new AlertDialog.Builder(TaskActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.list_name, null);

                    TextView title = view.findViewById(R.id.typing_name);

                    itemTaskName = view.findViewById(R.id.edit_text);
                    btn_saveTask = view.findViewById(R.id.submit_list);

                    title.setText("Edit Item : ");
                    itemTaskName.setText(NEWITEM.getTaskName());
                    btn_saveTask.setText("Update");

                    builder.setView(view);
                    alertDialog = builder.create();
                    alertDialog.show();


                    btn_saveTask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View s) {
                            //update our item
                            NEWITEM.setTaskName(itemTaskName.getText().toString());

                            if (!itemTaskName.getText().toString().isEmpty()) {
//                                recyclerViewTaskAdapter.notifyItemChanged(position,newItem);

                                taskList.remove(NEWITEM);
                                recyclerViewAdapterTask.notifyItemRemoved(position);

                                taskList.add(position, NEWITEM);
                                recyclerViewAdapterTask.notifyItemInserted(position);
                                alertDialog.dismiss();

                                Snackbar.make(view, "Updated succes", Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(view, "empty", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                    });

                    break;
            }
        }
    };
}
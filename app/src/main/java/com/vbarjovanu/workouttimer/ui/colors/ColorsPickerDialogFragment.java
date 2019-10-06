package com.vbarjovanu.workouttimer.ui.colors;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vbarjovanu.workouttimer.R;
import com.vbarjovanu.workouttimer.ui.generic.viewmodels.CustomViewModelFactory;

import java.util.Objects;

public class ColorsPickerDialogFragment extends DialogFragment {
    private DialogInterface.OnDismissListener mOnDismissListener;
    private IColorsPickerViewModel colorsPickerViewModel;
    private GridView gridView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity(), "Activity must be not null");

        this.colorsPickerViewModel = ViewModelProviders.of(this, CustomViewModelFactory.getInstance(this.getActivity().getApplication())).get(IColorsPickerViewModel.class);
        this.colorsPickerViewModel.getColors().observe(this, new Observer<Integer[]>() {
            @Override
            public void onChanged(Integer[] colors) {
                onColorsChanged(colors);
            }
        });
        this.colorsPickerViewModel.getSelectedColor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer selectedColor) {
                onSelectedColorChanged(selectedColor);
            }
        });
        return this.setupBuilder().create();
    }

    private AlertDialog.Builder setupBuilder() {
        Objects.requireNonNull(this.getActivity(), "Activity must not be null");
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.message_pick_a_color)
                .setView(createLayout())
                .setPositiveButton(R.string.select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onPositiveButtonClick(dialog, which);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onNegativeButtonClick(dialog, which);
                    }
                });
        return builder;
    }

    @Override
    public void onStart() {
        Integer selectedColor = null;
        int[] colors = null;
        super.onStart();
        Bundle arguments = this.getArguments();
        if (arguments != null) {
            if (arguments.containsKey("colors")) {
                colors = arguments.getIntArray("colors");
            }
            if (arguments.containsKey("color")) {
                selectedColor = arguments.getInt("color");
            }
        }
        this.colorsPickerViewModel.loadColors(colors);
        this.colorsPickerViewModel.selectColor(selectedColor);
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        if (getDialog() != null) {
            getDialog().dismiss();
            if (this.colorsPickerViewModel != null) {
                Bundle args = new Bundle();
                //noinspection ConstantConditions
                args.putInt("color", this.colorsPickerViewModel.getSelectedColor().getValue());
                ColorsPickerDialogFragment.this.setArguments(args);
            }
        }
    }

    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        if (getDialog() != null) {
            getDialog().cancel();
        }
    }

    @SuppressWarnings("unused")
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mOnDismissListener != null) mOnDismissListener.onDismiss(dialog);
    }

    private View createLayout() {
        gridView = new GridView(Objects.requireNonNull(this.getContext()));
        gridView.setPadding(10, 10, 10, 10);
        gridView.setGravity(Gravity.CENTER);
        gridView.setNumColumns(3);
        gridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        gridView.setHorizontalSpacing(10);
        gridView.setVerticalSpacing(10);
        gridView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onColorClick(adapterView, view, i, l);
            }
        });
        return gridView;
    }

    private void setupGridViewAdapter(Integer[] colors) {
        ListAdapter adapter;
        Objects.requireNonNull(this.getContext(), "Context must not be null");

        adapter = new ArrayAdapter<Integer>(this.getContext(), R.layout.fragment_dialog_color_picker_item, colors) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                @SuppressLint("ViewHolder")
                View view = inflater.inflate(R.layout.fragment_dialog_color_picker_item, parent, false);
                Integer color = this.getItem(position);
                if(color!=null) {
                    view.setBackgroundColor(color);
                }
                if (((GridView) parent).isItemChecked(position)) {
                    ((ImageView) view).setImageResource(R.drawable.baseline_done_24);
                }
                return view;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getItemViewType(int position) {
                return 0;
            }
        };
        this.gridView.setAdapter(adapter);
    }

    private void onColorsChanged(Integer[] colors) {
        this.setupGridViewAdapter(colors);
    }

    private void onSelectedColorChanged(Integer selectedColor) {
        int position = colorsPickerViewModel.getColorPosition(selectedColor);
        if (position >= 0) {
            gridView.setSelection(position);
            gridView.setItemChecked(position, true);
        }
    }

    private void onColorClick(AdapterView<?> adapterView, View view, int i, long l) {
        Integer[] colors = this.colorsPickerViewModel.getColors().getValue();
        Integer selectedColor = null;
        if (colors != null) {
            selectedColor = colors[i];
        }
        this.colorsPickerViewModel.selectColor(selectedColor);
    }
}

package com.systematics.buggee.Profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.ApiRequest;
import com.systematics.buggee.SimpleClasses.Callback;
import com.systematics.buggee.SimpleClasses.Functions;
import com.systematics.buggee.SimpleClasses.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class PageCategoryDialog extends DialogFragment implements View.OnClickListener {
    private Context context;
    RecyclerView subCategoryRecycler;
    SubCategoryAdapter subCategoryAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View v = inflater.inflate(R.layout.page_category_dialog, null);
        subCategoryRecycler = v.findViewById(R.id.subCategoryRecycler);

        RadioGroup radioGroup = v.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                subCategoryRecycler.setVisibility(View.VISIBLE);
                radioGroup.setVisibility(View.GONE);
                if (i == R.id.radioButton1) {
                    ArrayList<String> subCategories = new ArrayList<>();
                    subCategories.add("Bank");
                    subCategories.add("Bar");
                    subCategories.add("Book Store");
                    subCategories.add("Concert Venue");
                    subCategories.add("Food / Grocery");
                    subCategories.add("Hotel");
                    subCategories.add("Local Business");
                    subCategories.add("Movie Theatre");
                    subCategories.add("Museum/Art Gallery");
                    subCategories.add("Outdoor Gear/Sporting Goods");
                    subCategories.add("Real Estate");
                    subCategories.add("Restaurant / Cafe");
                    subCategories.add("School");
                    subCategories.add("Shopping / Retail");
                    subCategories.add("Spas/Beauty/Personal Care");
                    openSubCategory(subCategories);
                } else if (i == R.id.radioButton2) {
                    ArrayList<String> subCategories = new ArrayList<>();
                    subCategories.add("Automobiles and Parts");
                    subCategories.add("Church");
                    subCategories.add("Company");
                    subCategories.add("Computers/Technology");
                    subCategories.add("Consulting/Business Services");
                    subCategories.add("Cause");
                    subCategories.add("Food/Beverages");
                    subCategories.add("Health/Beauty");
                    subCategories.add("Insurance Company");
                    subCategories.add("Internet/Software");
                    subCategories.add("Legal/Law");
                    subCategories.add("Non-Profit Organization");
                    subCategories.add("Retail and Consumer Merchandise");
                    subCategories.add("Media/News/Publishing");
                    subCategories.add("Travel/Leisure");
                    openSubCategory(subCategories);
                } else if (i == R.id.radioButton3) {
                    ArrayList<String> subCategories = new ArrayList<>();
                    subCategories.add("App");
                    subCategories.add("Appliance");
                    subCategories.add("Baby Goods/Kids Goods");
                    subCategories.add("Cars");
                    subCategories.add("Clothing");
                    subCategories.add("Electronics");
                    subCategories.add("Food/Beverages");
                    subCategories.add("Furniture");
                    subCategories.add("Games/Toys");
                    subCategories.add("Health/Beauty");
                    subCategories.add("Jewelry/Watches");
                    subCategories.add("Kitchen/Cooking");
                    subCategories.add("Pet Supplies");
                    subCategories.add("Vitamins/Minerals");
                    openSubCategory(subCategories);
                } else if (i == R.id.radioButton4) {
                    ArrayList<String> subCategories = new ArrayList<>();
                    subCategories.add("Actor/Director");
                    subCategories.add("Artist");
                    subCategories.add("Athlete");
                    subCategories.add("Author");
                    subCategories.add("Business Person");
                    subCategories.add("Chef");
                    subCategories.add("Coach");
                    subCategories.add("Doctor");
                    subCategories.add("Entertainer");
                    subCategories.add("Journalist");
                    subCategories.add("Lawyer");
                    subCategories.add("Musician/Band");
                    subCategories.add("Politician");
                    subCategories.add("Teacher");
                    subCategories.add("Writer");
                    openSubCategory(subCategories);
                } else if (i == R.id.radioButton5) {
                    ArrayList<String> subCategories = new ArrayList<>();
                    subCategories.add("Album");
                    subCategories.add("Book");
                    subCategories.add("Concert Tour");
                    subCategories.add("Library");
                    subCategories.add("Magazine");
                    subCategories.add("Movie");
                    subCategories.add("Radio Station");
                    subCategories.add("Record Label");
                    subCategories.add("Sports Venue");
                    subCategories.add("TV Channel");
                    subCategories.add("TV Show");
                    openSubCategory(subCategories);
                } else if (i == R.id.radioButton6) {
                    ArrayList<String> subCategories = new ArrayList<>();
                    subCategories.add("Cause");
                    subCategories.add("Community");
                    openSubCategory(subCategories);
                }
            }
        });
        builder.setView(v);
        return builder.create();
    }

    private void openSubCategory(ArrayList<String> subCategories) {
        subCategoryAdapter = new SubCategoryAdapter(context, subCategories, new SubCategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String item, View view) {
                updatePageCategory(item);
            }
        });
        subCategoryRecycler.setLayoutManager(new LinearLayoutManager(context));
        subCategoryRecycler.setAdapter(subCategoryAdapter);
    }

    private void updatePageCategory(String item) {
        JSONObject parameters = new JSONObject();
        try {

            parameters.put("id", Variables.user_id);
            parameters.put("category", item);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.updatePageCategory, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Functions.showToast(getActivity(), "Updated!");
                    } else {
                        Functions.showToast(getActivity(), "Failed!");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        dismiss();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }
}

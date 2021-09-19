package com.example.isumbong;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static com.example.isumbong.fragment_accident_info.text_license;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rakshakhegde.stepperindicator.StepperIndicator;

import java.util.ArrayList;

//import com.anton46.stepsview.StepsView;


public class public_report_now extends AppCompatActivity {

    static int state = 0;
    FragmentTransaction ft;
    SharedPreferences prefs;
    SharedPreferences.Editor edit;
    Fragment fragment;
    static FloatingActionButton next;
    static FloatingActionButton prev;
    database db;
    INPUTS input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_report_now);
//        //shared preferences
//        prefs = getSharedPreferences("mypref",0);
//
//        try {
//            if(prefs.contains("plate")){
//                plate.setText(prefs.getString("plate",""),TextView.BufferType.EDITABLE);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        input = new INPUTS();
        db = new database(this);
        //Stepper
        StepperIndicator stpi = findViewById(R.id.stepperIndicator);
        String[] steps = {"VICTIMS\nINFO", "ACCIDENT\nINFO", "VEHICLE\nINFO", "LOCATION", "STATEMENT","CONFIRM"};
        stpi.setLabels(steps);
        stpi.showLabels(true);
       // stpi.setLabelSize(26);
        stpi.setLabelColor(public_report_now.this.getResources().getColor(R.color.black));
        stpi.setStepCount(steps.length);
        stpi.setIndicatorColor(public_report_now.this.getResources().getColor(R.color.green1));
        stpi.setLineColor(public_report_now.this.getResources().getColor(R.color.grey));
        stpi.setLineDoneColor(public_report_now.this.getResources().getColor(R.color.green1));
        stpi.setShowDoneIcon(true);



        next = findViewById(R.id.button);
        prev = findViewById(R.id.button_prev);

        //DEFAULT FRAG

        numvitims();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (state < (steps.length-1)) {

                    state = state + 1;
                    stpi.setCurrentStep(state);
                    //    Toast.makeText(public_report_now.this, "STATE"+state, Toast.LENGTH_SHORT).show();

//                    if (state == 2) {
                        if (fragment instanceof fragment_victim_details){
//                            Toast.makeText(public_report_now.this, ""+input.getText_license(), Toast.LENGTH_SHORT).show();
                            if(input.getText_license()==null || input.getText_license() == ""){
                                fragment = new fragment_accident_info();
                            }
                            else
                                fragment = new fragment_accident_info(input.getText_license());
                        }

//                    } else if (state == 3) {
                        else if (fragment instanceof fragment_accident_info){
                            input.setText_license(text_license.getText().toString());
                            fragment = new fragment_vehicle_info();
//                            ArrayList<String> age = fragment_victim_details.inputAge;
//                            for(String ag:age){
//                                Toast.makeText(public_report_now.this, ag, Toast.LENGTH_SHORT).show();
//                            }
                        }

//                    } else if (state == 4) {
                        else if (fragment instanceof fragment_vehicle_info){
                            fragment = new MapsFragment();
                        }

                        else if(fragment instanceof MapsFragment){
                            fragment = new fragment_statement();
//                            Toast.makeText(public_report_now.this, ""+getLocation+"\n "+getCoordinatesLat+
//                                    "\n "+getCoordinatesLng, Toast.LENGTH_SHORT).show();
                        }

                        else if(fragment instanceof fragment_statement){
                            fragment = new fragment_confirm();
                            View viewC = getLayoutInflater().inflate(R.layout.builder_confirmation,null);

                            //insert inputs
                            getvictimdetails(viewC);


                            AlertDialog.Builder builder1 = new AlertDialog.Builder(public_report_now.this);
                        builder1.setTitle("CONFRIMATION")
                                .setView(viewC)
                                .setCancelable(false);

                        builder1.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //stepper
                                state -=1;
                                stpi.setCurrentStep(state);
                                getSupportFragmentManager().popBackStack();
                            }
                        });
                        builder1.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //stepper and redirect
                                if(fragment !=null){
                                    fragment = new fragment_serial();
                                    ft = getSupportFragmentManager().beginTransaction();
                                    ft.add(R.id.fragment_container, fragment)
                                            .addToBackStack(null)
                                            .commit();
                                    //state+=1;
                                    stpi.setCurrentStep(state+1);
                                    next.hide();
                                    prev.hide();

                                    boolean check =db.InsertVictims(Integer.parseInt(fragment_novictims.vnum));
                                    if (check){
                                        int id = db.victimsID();
                                        ArrayList<Victim> victims = fragment_victim_details.victims;
                                        for(int j=0;j<victims.size();j++){
                                            if(check)
                                                check = db.InsertVictimInfo(victims.get(j),id);
                                            else
                                                break;
                                        }
                                    }
                                }
                            }
                        });

                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();
                    }

                    ft = getSupportFragmentManager().beginTransaction();
                    ft.add(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }

            }
        });


        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (state >= -1) {

                    if(state==0){
                        next.hide();
                        prev.hide();
                    }


                    else{
                        state = state - 1;
                       // Toast.makeText(public_report_now.this, "STATE"+state, Toast.LENGTH_SHORT).show();
                        stpi.setCurrentStep(state);
                    //    text_license.setText(getIntent().getStringExtra("license"));
                    }


                }

                getSupportFragmentManager().popBackStack();

            }
        });
    }

    @Override
    public void onBackPressed() {
        ft = getSupportFragmentManager().beginTransaction();
        fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(fragment instanceof fragment_novictims){
            Intent intent = new Intent(public_report_now.this, public_homepage.class);
            startActivity(intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP));
        }
        else
            super.onBackPressed();
    }

    public void numvitims(){
        ft = getSupportFragmentManager().beginTransaction();
        fragment_novictims fnum = new fragment_novictims();
        ft.add(R.id.fragment_container, fnum, "fnum")
                .addToBackStack(null);
        ft.commit();
        next.hide();
        prev.hide();

    }

    private void getvictimdetails(View viewC){
        ArrayList<Victim> victim = fragment_victim_details.victims;
        String name1 ="";
        for(Victim v : victim){
            name1 += v.toString() + "\n";
        }
        TextView name = viewC.findViewById(R.id.textView_info);
        name.setText(name1);

    }


}
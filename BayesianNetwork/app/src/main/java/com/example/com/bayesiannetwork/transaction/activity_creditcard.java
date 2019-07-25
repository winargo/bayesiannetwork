package com.example.com.bayesiannetwork.transaction;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.com.bayesiannetwork.Luhn;
import com.example.com.bayesiannetwork.R;
import com.example.com.bayesiannetwork.core;
import com.example.com.bayesiannetwork.formula.thousandedittext;
import com.example.com.bayesiannetwork.object.creditcard;

import java.util.Calendar;
import java.util.Date;

public class activity_creditcard extends AppCompatActivity {

    EditText card_number,card_type,card_first,billing,cvv,month,year,limit;

    Button savecard,finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_creditcard);

        card_number = findViewById(R.id.rccardnumber);
        card_type = findViewById(R.id.rccard_type);
        card_first = findViewById(R.id.rcfirstname);
        billing = findViewById(R.id.rcaddress);
        cvv = findViewById(R.id.rccvv);
        month = findViewById(R.id.rcmonth);
        year = findViewById(R.id.rcyear);

        limit = findViewById(R.id.rclimit);

        limit.addTextChangedListener(new thousandedittext(limit));

        savecard = findViewById(R.id.rcsave);
        finish = findViewById(R.id.rcfinish);
        card_number.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char space = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (card_number.getText().toString().equals("")){

                }
                else{
                    if(card_number.getText().toString().substring(0,1).equals("4")){
                        card_type.setText("Visa");
                    }
                    else if(card_number.getText().toString().substring(0,1).equals("5")){
                        card_type.setText("MasterCard");
                    }
                    else{
                        card_type.setText("Unknown");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() > 0 && (s.length() % 5) == 0) {
                    final char c = s.charAt(s.length() - 1);
                    if (space == c) {
                        s.delete(s.length() - 1, s.length());
                    }
                }
                // Insert char where needed.
                if (s.length() > 0 && (s.length() % 5) == 0) {
                    char c = s.charAt(s.length() - 1);
                    // Only if its a digit where there should be a space we insert a space
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                        s.insert(s.length() - 1, String.valueOf(space));
                    }
                }
            }

        });

        savecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(card_number.getText().toString().equals("")){
                    Toast.makeText(activity_creditcard.this, "Card Number Can't Be empty", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(Luhn.Check(card_number.getText().toString())==true){
                        if(card_first.getText().toString().equals("")){
                            Toast.makeText(activity_creditcard.this, "Card Name Can't Be empty", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            if(billing.getText().toString().equals("")){
                                Toast.makeText(activity_creditcard.this, "Billing Address Can't Be empty", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                if(cvv.getText().toString().equals("")){
                                    Toast.makeText(activity_creditcard.this, "CVV Can't Be empty", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    if(month.getText().toString().equals("")){
                                        Toast.makeText(activity_creditcard.this, "Card Expiracy Month Can't Be empty", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        if(Integer.valueOf(month.getText().toString())>12 || Integer.valueOf(month.getText().toString())<1){
                                            Toast.makeText(activity_creditcard.this, "Card Expiracy month is invalid, specify ranges between (01-12)", Toast.LENGTH_SHORT).show();
                                        }
                                        else{
                                            if(year.getText().toString().equals("")){
                                                Toast.makeText(activity_creditcard.this, "Card Expiracy Year Can't Be empty", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Calendar cal = Calendar.getInstance();
                                                cal.setTime(new Date());

                                                Calendar cal1 = Calendar.getInstance();
                                                cal1.setTime(new Date());

                                                int years = cal.get(Calendar.YEAR);
                                                int yearslater = years+50;
                                                cal1.set(Calendar.YEAR,Integer.valueOf(year.getText().toString()));
                                                cal1.set(Calendar.MONDAY,Integer.valueOf(month.getText().toString()));
                                                if(Integer.valueOf(year.getText().toString())>yearslater || Integer.valueOf(year.getText().toString())<years || cal1.getTime().equals(cal.getTime())){
                                                    Toast.makeText(activity_creditcard.this, "Card Expiracy Year is invalid or expired, specify ranges between ("+years+"-"+yearslater+")", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    if(limit.getText().toString().equals("")){
                                                        Toast.makeText(activity_creditcard.this, "Card Transaction Limit Can't Be empty", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else{
                                                        creditcard data = new creditcard();
                                                        data.setCard_billing(billing.getText().toString());
                                                        data.setCard_name(card_first.getText().toString());
                                                        data.setCard_number(card_number.getText().toString());
                                                        data.setCard_type(card_type.getText().toString());
                                                        data.setCard_billing(billing.getText().toString());
                                                        data.setCvv(Integer.parseInt(cvv.getText().toString()));
                                                        data.setMonth(Integer.parseInt(month.getText().toString()));
                                                        data.setYear(Integer.parseInt(year.getText().toString()));
                                                        data.setLimit(Integer.parseInt(limit.getText().toString().replace(" ","").replace(",","")));
                                                        core.alllistcreditcard.add(data);
                                                        Toast.makeText(activity_creditcard.this, core.alllistcreditcard.size()+" Credit card added", Toast.LENGTH_SHORT).show();
                                                        card_number.setText("");
                                                        card_type.setText("Unknown");
                                                        card_first.setText("");
                                                        billing.setText("");
                                                        cvv.setText("");
                                                        month.setText("");
                                                        year.setText("");
                                                        limit.setText("");
                                                    }
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                        }
                    }
                    else{
                        Toast.makeText(activity_creditcard.this, "Invalid Card Number", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.ehealth_projectg1.model.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ehealth_projectg1.model.data.Day;
import com.example.ehealth_projectg1.view.AuthenticationActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.ehealth_projectg1.model.data.UserItem;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseRepository extends AuthenticationActivity {

    //Temp File Strings
    private String SHARED_FILE = "UserData";
    private String KEY_NAME = "name";
    private String KEY_EMAIL = "email";
    private String KEY_HARDWARE = "serialHardware";

    //FireBase Roots Strings
    private static String DATABASE_USERS = "Users";
    private static String DATABASE_CONTROLPARAM = "ControlParameters";
    private static String DATABASE_WEEKMEDICATION = "WeekMedication";

    //Firebase Roots Children Strings
    //weeKMedication Childrens
    private static String FIELD_MED_NAME = "medicationName";
    private static String FIELD_MED_TAKEN = "medicationTaken";

    //Control Parameters Childrens
    private static String FIELD_WATER_AMOUNT = "waterAmount";
    private static String FIELD_TEMP = "temperature";
    private static String FIELD_FALL = "fall";
    private static String FIELD_TOKEN = "token";

    //Users Childrens
    private static String FIELD_NAME = "name";
    private static String FIELD_EMAIL = "email";
    private static String FIELD_HARDWARE = "hardware";

    //Observables
    private MutableLiveData<String> logInObservable;
    private MutableLiveData<String> recovPassObservable;
    private MutableLiveData<String> registerObservable;
    private MutableLiveData<String> waterObservable;
    private MutableLiveData<String> fallenObservable;
    private MutableLiveData<String> tempObservable;
    private MutableLiveData<Day[]> medicationObservable;
    private MutableLiveData<Integer> addOkMedicationObservable;

    //Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference database;

    //To save the query message
    private String taskMessage = "";
    private static final String FAILED = "FAILED";
    private int userHardware = 0;

    //To open or close the water tap -> 5 positions 0%, 25%, 50%, 75%, 100%
    private static final int QUANTITY_WATER = 25;

    public FirebaseRepository() {
        //Declare and init database
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

    }

    //-------------- INIT USER INFO -----------------

    public int getUserHardware(Context context) {
        //Get shared preferences
        SharedPreferences pref = context.getSharedPreferences(SHARED_FILE, MODE_PRIVATE);

        //Get hardware number
        String serialHardware = pref.getString(KEY_HARDWARE, null);

        return Integer.parseInt(Objects.requireNonNull(serialHardware));
    }

    public void setTempFile(UserItem userItem, Context context) {

        //Get shared preferences
        SharedPreferences pref = context.getSharedPreferences(SHARED_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor;
        prefEditor = pref.edit();

        //Write the information of the user
        prefEditor.putString(KEY_NAME, userItem.getName());
        prefEditor.putString(KEY_EMAIL, userItem.getEmail());
        prefEditor.putString(KEY_HARDWARE, String.valueOf(userItem.getHardware()));

        prefEditor.apply();
    }

    public void getUserInfo(final Context context) {

        //Get information of the user
        final String uId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(DATABASE_USERS);

        userRef.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                if (map == null) return;

                //Create userItem with the information of the user to share
                userHardware = Integer.parseInt(Objects.requireNonNull(map.get(FIELD_HARDWARE)).toString());
                String userName = Objects.requireNonNull(map.get(FIELD_NAME)).toString();
                String userEmail = Objects.requireNonNull(map.get(FIELD_EMAIL)).toString();
                UserItem userItem = new UserItem(userName, userEmail, userHardware);
                setTempFile(userItem, context);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //---------- LOG IN ----------------------

    public LiveData<String> getLogIn() {
        //Init observable
        if (logInObservable == null) {
            logInObservable = new MutableLiveData<>();
        }
        return logInObservable;
    }

    public void logIn(String email, final String password, final Context context) {
        //mAuth to consult email and password for login
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //get user info to save it on temp file
                    getUserInfo(context);

                    //correct login
                    taskMessage = "1/";
                    logInObservable.postValue(taskMessage);

                } else {
                    //the credentials are wrong --> NO LOG IN
                    taskMessage = "0/";

                    //Reading the cause exception
                    String cause = Objects.requireNonNull(task.getException()).getMessage();
                    taskMessage = taskMessage + cause;
                    logInObservable.postValue(taskMessage);
                }
            }
        });
    }
    //--------------- SIGN UP --------------------------

    public LiveData<String> getRegister() {
        //Init observable
        if (registerObservable == null) {
            registerObservable = new MutableLiveData<>();
        }
        return registerObservable;
    }

    public void newRegister(final UserItem newUser, final String newPassword) {

        mAuth.signInWithEmailAndPassword(newUser.getEmail(), newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //the user already exists
                    taskMessage = "0/";
                    registerObservable.postValue(taskMessage);
                }
                //create user
                else {
                    mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull final Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final String uId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                database.child(DATABASE_USERS).child(uId).setValue(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                //init firebase for the user
                                                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                DatabaseReference refUser = database.getReference(DATABASE_USERS).child(uId);

                                                // Attach a listener to init the firebase fields that are classified by serial hardware
                                                refUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    DatabaseReference refControlParamenters = database.getReference(DATABASE_CONTROLPARAM);
                                                    DatabaseReference refWeekMedication = database.getReference(DATABASE_WEEKMEDICATION);

                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        @SuppressWarnings("unchecked")
                                                        Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                                                        if (map == null) return;
                                                        int userHardware = Integer.parseInt(Objects.requireNonNull(map.get(FIELD_HARDWARE)).toString());

                                                        //Init controlParameters fields with the userHardware value
                                                        refControlParamenters.child(String.valueOf(userHardware)).child(FIELD_WATER_AMOUNT).setValue("0");
                                                        refControlParamenters.child(String.valueOf(userHardware)).child(FIELD_TEMP).setValue("22");
                                                        refControlParamenters.child(String.valueOf(userHardware)).child(FIELD_FALL).setValue("false");

                                                        //Init weekMedication fields with the userHardware value
                                                        String dayOfWeek;
                                                        Map<String, Object> weekMedicationData = new HashMap<>();
                                                        String medicationName = "";
                                                        int medicationTaken = 0;
                                                        int weekdays = 7;
                                                        for (int i = 0; i < weekdays; i++) {
                                                            //Init fields from the day using DayOfWeek default enum class
                                                            dayOfWeek = String.valueOf(DayOfWeek.of(i + 1));
                                                            weekMedicationData.put(FIELD_MED_NAME, medicationName);
                                                            weekMedicationData.put(FIELD_MED_TAKEN, medicationTaken);
                                                            refWeekMedication.child(String.valueOf(userHardware)).child(dayOfWeek).setValue(weekMedicationData);

                                                        }
                                                        //Post "1/" -> Write was successful!
                                                        taskMessage = "1/";
                                                        registerObservable.postValue(taskMessage);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        //post "3/" -> if write onCancelled
                                                        taskMessage = "3/";
                                                        registerObservable.postValue(taskMessage);
                                                    }
                                                });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //BBDD Write failed
                                                taskMessage = "3/";
                                                registerObservable.postValue(taskMessage);
                                            }
                                        });
                            } else {
                                //If write fail by other exceptions post "2/" plus the exception message to notify the cause to user
                                String cause = Objects.requireNonNull(task.getException()).getMessage();
                                taskMessage = "2/" + cause;
                                registerObservable.postValue(taskMessage);
                            }
                        }
                    });
                }
            }
        });
    }

    //---------------RECOVER PASSWORD---------------------

    public LiveData<String> getRecoverPassword() {
        //Init observable
        if (recovPassObservable == null) {
            recovPassObservable = new MutableLiveData<>();
        }
        return recovPassObservable;
    }

    public void emailChangePassword(final String email) {
        //To recover the password by email before login
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Email send -> post 1
                            taskMessage = "1/";
                            recovPassObservable.postValue(taskMessage);
                        } else {
                            //Email not send -> post0
                            taskMessage = "0/";
                            recovPassObservable.postValue(taskMessage);
                        }
                    }

                });
    }

    public void changePassword(final String email, final String oldPassword, final String newPassword) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Email and oldpassword are correct
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //1 case -> all parameters are correct, and tast complete successful
                                                taskMessage = "1/";
                                                recovPassObservable.postValue(taskMessage);
                                            } else {
                                                //2 cases are when problems are relacionated with newPassword
                                                String cause = task.getException().getLocalizedMessage();
                                                taskMessage = "2/" + cause;
                                                recovPassObservable.postValue(taskMessage);
                                            }
                                        }
                                    });
                        } else {
                            //3 cases are when problems are relacionated with email and oldpassword
                            taskMessage = "3/";
                            String cause = task.getException().getLocalizedMessage();
                            taskMessage = taskMessage + cause;
                            recovPassObservable.postValue(taskMessage);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //3 cases are when problems are relacionated with email and oldpassword
                        //Email and oldpassword are not correct
                        taskMessage = "3/";
                        String cause = e.getMessage();
                        taskMessage = taskMessage + cause;
                        recovPassObservable.postValue(taskMessage);
                    }
                });
    }

    //---------------- DISPLAY PARAMS ------------------------------

    public LiveData<String> getWater(final Context context) {
        //Init observable
        if (waterObservable == null) {
            waterObservable = new MutableLiveData<>();
        }

        //Get information from database using a listener to the database controlParam
        final DatabaseReference controlParametersRef = FirebaseDatabase.getInstance().getReference(DATABASE_CONTROLPARAM);
        controlParametersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userHardware = getUserHardware(context);
                String waterAmount = Objects.requireNonNull(dataSnapshot.child(String.valueOf(userHardware))
                        .child(FIELD_WATER_AMOUNT).getValue().toString());
                waterObservable.postValue(waterAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return waterObservable;
    }

    public LiveData<String> getFallen(final Context context) {
        //Init observable
        if (fallenObservable == null) {
            fallenObservable = new MutableLiveData<>();
        }

        //Get information from database using a listener to the database controlParam
        final DatabaseReference controlParametersRef = FirebaseDatabase.getInstance().getReference(DATABASE_CONTROLPARAM);
        controlParametersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Post string if fallen or not
                userHardware = getUserHardware(context);
                String fallenString = Objects.requireNonNull(dataSnapshot.child(String.valueOf(userHardware))
                        .child(FIELD_FALL).getValue().toString());
                fallenObservable.postValue(fallenString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //In case of wrong lecture
                fallenObservable.postValue(FAILED);
            }
        });
        return fallenObservable;
    }

    public LiveData<String> getTemperature(final Context context) {
        //Init observable
        if (tempObservable == null) {
            tempObservable = new MutableLiveData<>();
        }

        //Get information from database using a listener to the database controlParam
        final DatabaseReference controlParametersRef = FirebaseDatabase.getInstance().getReference(DATABASE_CONTROLPARAM);
        controlParametersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Post value of temperature
                userHardware = getUserHardware(context);
                String tempValue = Objects.requireNonNull(dataSnapshot.child(String.valueOf(userHardware))
                        .child(FIELD_TEMP).getValue().toString());
                tempObservable.postValue(tempValue);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //In case of wrong lecture
                tempObservable.postValue(FAILED);
            }
        });
        return tempObservable;
    }

    public void waterQuantity(int actionTap, int tapStage, Context context) {
        //it opens and close the tap trought 5 stages -> 0%, 25%, 50%, 75% and 100%
        //actionTap can be 1 or 2. If 1 -> opens the tap ; if 2 (else)-> close the tap
        // waterAmount recolects the actual stage value set from firebase when the page is loading
        userHardware = getUserHardware(context);

        if (actionTap == 1) {
            //Add % to water
            if (tapStage + QUANTITY_WATER <= 100) {
                tapStage += QUANTITY_WATER;

                //Update waterAmount statge to firebase
                database.child(DATABASE_CONTROLPARAM).child(String.valueOf(userHardware)).child(FIELD_WATER_AMOUNT).setValue(tapStage)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Write was successful
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Write failed
                                waterObservable.postValue(FAILED);
                            }
                        });
            }
        } else {
            //Substract % to water
            if (tapStage - QUANTITY_WATER >= 0) {
                tapStage -= QUANTITY_WATER;

                database.child(DATABASE_CONTROLPARAM).child(String.valueOf(userHardware)).child(FIELD_WATER_AMOUNT).setValue(tapStage)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Write was successful!
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Write failed
                                waterObservable.postValue(FAILED);
                            }
                        });
            }
        }
    }

    //----------------- GET MEDICATION VIEW ----------------------

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<Day[]> getMedication(Context context) {
        //Init observable
        if (medicationObservable == null) {
            medicationObservable = new MutableLiveData<>();
        }

        //Init parameters
        userHardware = getUserHardware(context);
        final Day[] weekMedication = new Day[7];

        //Get information from database
        final DatabaseReference weekMedicationReference = FirebaseDatabase.getInstance().getReference(DATABASE_WEEKMEDICATION);
        weekMedicationReference.child(String.valueOf(userHardware)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Read value for all week
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                        //Get day name
                        String dayOfWeek = messageSnapshot.getKey();
                        int index = DayOfWeek.valueOf(dayOfWeek).getValue();
                        dayOfWeek = Objects.requireNonNull(dayOfWeek).substring(0, 1) +
                                Objects.requireNonNull(dayOfWeek).substring(1).toLowerCase();

                        //Get medication
                        String medicationName = Objects.requireNonNull(messageSnapshot.child(FIELD_MED_NAME).getValue()).toString();

                        //Get flag if medication was taken
                        int medicationTaken = Integer.parseInt(Objects.requireNonNull(messageSnapshot.child(FIELD_MED_TAKEN).getValue()).toString());

                        //Add day to arraylist
                        Day day = new Day(dayOfWeek, medicationName, medicationTaken);
                        weekMedication[index - 1] = day;
                    }
                }
                medicationObservable.postValue(weekMedication);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //In case of wrong lecture
                medicationObservable.postValue(weekMedication);
            }
        });

        return medicationObservable;
    }

    //----------------- ADD MEDICATION  ----------------------

    public LiveData<Integer> getAddOkMedication() {
        //Init observable
        if (addOkMedicationObservable == null) {
            addOkMedicationObservable = new MutableLiveData<>();
        }
        return addOkMedicationObservable;
    }

    //RequiresApi needed for DayOfWeek class implementation
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addMedicationToCalendar(ArrayList<String> weekMedication, Context context) {
        //Init parameters
        final boolean[] successful = {true};
        String dayOfWeek;
        Map<String, Object> weekMedicationData = new HashMap<>();
        String medicationName;
        int medicationTaken = 0;

        userHardware = getUserHardware(context);

        for (int i = 0; i < weekMedication.size(); i++) {
            //Get information from the day
            dayOfWeek = String.valueOf(DayOfWeek.of(i + 1));
            medicationName = weekMedication.get(i);
            weekMedicationData.put(FIELD_MED_NAME, medicationName);
            weekMedicationData.put(FIELD_MED_TAKEN, medicationTaken);

            //Add information to firebase
            database.child(DATABASE_WEEKMEDICATION).child(String.valueOf(userHardware)).child(dayOfWeek).setValue(weekMedicationData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Write was successful!
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Error
                            successful[0] = false;
                        }
                    });


        }
        //Post value to inform if was successful
        if (successful[0]) {
            addOkMedicationObservable.postValue(1);
        } else addOkMedicationObservable.postValue(0);
    }

    //----------------- HTTP MESSAGING  ----------------------
    public void saveTokenHTTPMessaging(String token, Context context){
        userHardware = getUserHardware(context);

        database.child(DATABASE_CONTROLPARAM).child(String.valueOf(userHardware)).child(FIELD_TOKEN).setValue(token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //TODO posar observable?
                        //Write was successful!
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Write failed
                        //observable.postValue(FAILED);
                    }
                });
    }

}

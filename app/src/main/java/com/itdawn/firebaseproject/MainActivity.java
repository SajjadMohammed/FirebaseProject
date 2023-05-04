package com.itdawn.firebaseproject;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itdawn.firebaseproject.databinding.ActivityMainBinding;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseStorage storage;
    ActivityMainBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();


        //Employee employee = new Employee("Ahmed", "Mohammed", "Kirkuk", "34");

        //
        //mainBinding.addToFireStore.setOnClickListener(view -> addToFireStore(employee));
        //
        mainBinding.uploadImageWithData.setOnClickListener(view -> uploadImageWithData());

        //
        mainBinding.getFromFireStore.setOnClickListener(view -> getFromFireStore());

    }

    private void uploadImageWithData() {
        StorageReference storageReference = storage.getReference();

        StorageReference uploadReference = storageReference.child("employeeImage/h.png");

        UploadTask uploadTask = uploadReference.putBytes(getPictureBytes());

        uploadTask.continueWithTask(task -> {
                    if (task.isSuccessful())
                        return uploadReference.getDownloadUrl();
                    return null;
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String imageUri = task.getResult().toString();

                        Employee employee = new Employee("Aymen", "Ahmed", "Kirkuk",
                                "23", imageUri);

                        addToFireStore(employee);

                    } else {
                        Log.println(Log.INFO, "task", Objects.requireNonNull(task.getException()).toString());
                    }
                });
    }


    private byte[] getPictureBytes() {
        Bitmap bitmap = ((BitmapDrawable) (mainBinding.sampleImage.getDrawable())).getBitmap();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        float scaleWidth = ((float) 480) / width;
        float scaleHeight = ((float) 480) / height;
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 70, outputStream);

        return outputStream.toByteArray();
    }

    private void getFromFireStore() {
        List<Employee> employeeList = new ArrayList<>();
        //
        db.collection("Departments")
                .document("CS")
                .collection("Employees")
                .get()
                .addOnCompleteListener(task -> {
                    QuerySnapshot querySnapshot = task.getResult();

                    String firstName = querySnapshot.getDocuments().get(0).getString("firstName");
                    Toast.makeText(this, firstName, Toast.LENGTH_LONG)
                            .show();

//                    for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
//                        employeeList.add(new Employee(
//                                documentSnapshot.getString("firstName"),
//                                documentSnapshot.getString("lastName"),
//                                documentSnapshot.getString("age"),
//                                documentSnapshot.getString("address")
//                        ));
//                    }
//                    //
//                    Toast.makeText(this, employeeList.get(1).firstName, Toast.LENGTH_LONG)
//                            .show();
                });
    }

    private void addToFireStore(Employee employee) {
        // Add a new document with a generated ID
        db.collection("Departments")
                .document("CS")
                .collection("Employees")
                .document()
                .set(employee)
                .addOnCompleteListener(task -> {
                    if (task.isComplete()) {
                        Toast.makeText(this, "Data uploaded to fire store", Toast.LENGTH_LONG).show();
                    }
                });
    }


    private static class Employee {
        String firstName, lastName, address, age, imageUri;

        public Employee(String firstName, String lastName, String address, String age, String imageUri) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.age = age;
            this.imageUri = imageUri;
        }

        public String getImageUri() {
            return imageUri;
        }

        public void setImageUri(String imageUri) {
            this.imageUri = imageUri;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }
    }
}
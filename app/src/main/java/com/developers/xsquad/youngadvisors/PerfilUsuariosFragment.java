package com.developers.xsquad.youngadvisors;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.developers.xsquad.youngadvisors.Utilities.Adaptadores.AdapterComentarios;
import com.developers.xsquad.youngadvisors.Utilities.Calificador;
import com.developers.xsquad.youngadvisors.Utilities.CalificadorData;
import com.developers.xsquad.youngadvisors.Utilities.DataPerfil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class PerfilUsuariosFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    double Total = 0;
    int ContadorComentarios = 0;

    TextView Nombre, Carrera, Telefono, Sobremi, Correo;
    ImageView Foto, Estrellas_Perfil;
    Bundle bundle;
    String UI;
    ArrayList<Calificador> calificadors;

    FirebaseStorage storage;
    StorageReference storageRef;
    StorageReference mountainsRef;
    DatabaseReference mDatabase;
    RecyclerView RComentarios;
    AdapterComentarios adapterComentarios;
    LinearLayoutManager llm;
    FragmentTransaction fragmentTransaction;
    ProgressDialog progressDialog;
    ImageView IVCall;
    ImageView IVCorreo;
    DataPerfil dataPerfil;
    private OnFragmentInteractionListener mListener;

    public PerfilUsuariosFragment() {

    }

    public static PerfilUsuariosFragment newInstance(String param1, String param2) {
        PerfilUsuariosFragment fragment = new PerfilUsuariosFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_usuarios, container, false);
        bundle = getArguments();
        UI = bundle.getString("UI");
        calificadors = new ArrayList<Calificador>();
        Foto = view.findViewById(R.id.IVPerfilFotoUsuarios);
        Nombre = view.findViewById(R.id.TVPerfilNombreUsuarios);
        Carrera = view.findViewById(R.id.TVPerfilCarreraUsuarios);
        Correo = view.findViewById(R.id.TVPerfilCorreoUsuarios);
        Telefono = view.findViewById(R.id.TVPerfilTelefonoUsuario);
        Sobremi = view.findViewById(R.id.TVPerfilSobremiUsuarios);
        Estrellas_Perfil = view.findViewById(R.id.IVPerfilEstrellasUsuarios);
        RComentarios = view.findViewById(R.id.ResultadoComentarios);
        progressDialog = new ProgressDialog(getContext());
        IVCall = view.findViewById(R.id.IVPerfilLlamarUsuarios);
        IVCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("tel:" + dataPerfil.getTelefono());
                    Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        IVCorreo = view.findViewById(R.id.IVPerfilCorreo);
        IVCorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, dataPerfil.getCorreo());
                emailIntent.putExtra(Intent.EXTRA_CC, "YA");
                emailIntent.putExtra(Intent.EXTRA_BCC, "YA");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacto");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Querid@ "
                        + dataPerfil.getNombre() + " " + dataPerfil.getApellido()
                        + "\n"
                        + "[...]");

                try {
                    startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(v.getContext(),
                            "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        progressDialog.setMessage("Cargnado ...");
        progressDialog.show();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("proyecto/db/perfir/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot: dataSnapshot.getChildren()){
                    mDatabase.child("perfir/").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            try {
                                if(snapshot.getKey().equals(UI)) {
                                    dataPerfil = snapshot.getValue(DataPerfil.class);
                                    Nombre.setText(dataPerfil.getNombre() + " " + dataPerfil.getApellido());
                                    Carrera.setText(dataPerfil.getCarrera());
                                    Correo.setText(dataPerfil.getCorreo());
                                    Telefono.setText(dataPerfil.getTelefono());
                                    Sobremi.setText(dataPerfil.getSobremi());
                                }
                            }catch (Exception e){
                            }
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });
        DescargarImagen(UI);
        //Cargar comentarios...
        progressDialog.setMessage("Cargando comentarios...");
        progressDialog.show();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("proyecto/db/calificaciones/").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(final DataSnapshot snapshot: dataSnapshot.getChildren()){
                    mDatabase.child(snapshot.getKey() + "/calificador/").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(UI.equals(snapshot.getKey())) {
                                for (final DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    mDatabase.child(snapshot1.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for(final DataSnapshot snapshot2 : snapshot1.getChildren()) {
                                                mDatabase.child(snapshot2.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        try {
                                                            final Calificador calificador = new Calificador(snapshot2.getKey(), snapshot2.getValue(CalificadorData.class));
                                                            calificadors.add(calificador);
                                                            Total += calificador.getCalificacion();
                                                            ContadorComentarios++;
                                                            adapterComentarios = new AdapterComentarios(calificadors, getContext());
                                                            adapterComentarios.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    PerfilUsuariosFragment perfilUsuariosFragment = new PerfilUsuariosFragment();
                                                                    fragmentTransaction = getFragmentManager().beginTransaction();
                                                                    Bundle args = new Bundle();
                                                                    args.putString("UI", calificadors.get(RComentarios.getChildAdapterPosition(v)).getId());
                                                                    perfilUsuariosFragment.setArguments(args);
                                                                    fragmentTransaction.replace(R.id.fragment, perfilUsuariosFragment);
                                                                    fragmentTransaction.addToBackStack(null);
                                                                    fragmentTransaction.commit();
                                                                }
                                                            });
                                                            llm = new LinearLayoutManager(getContext());
                                                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                                                            RComentarios.setLayoutManager(llm);
                                                            RComentarios.setAdapter(adapterComentarios);

                                                            switch ((int)(Total/ContadorComentarios))
                                                            {
                                                                case 1:
                                                                    Estrellas_Perfil.setImageResource(R.drawable.estrella_1);
                                                                    break;
                                                                case 2:
                                                                    Estrellas_Perfil.setImageResource(R.drawable.estrella_2);
                                                                    break;
                                                                case 3:
                                                                    Estrellas_Perfil.setImageResource(R.drawable.estrella_3);
                                                                    break;
                                                                case 4:
                                                                    Estrellas_Perfil.setImageResource(R.drawable.estrella_4);
                                                                    break;
                                                                case 5:
                                                                    Estrellas_Perfil.setImageResource(R.drawable.estrella_5);
                                                                    break;
                                                                default:
                                                                    Estrellas_Perfil.setImageResource(R.drawable.estrella_1);
                                                                    break;
                                                            }

                                                        } catch (Exception e) {
                                                            Toast.makeText(getContext(), "Error: \n" + e.toString(), Toast.LENGTH_LONG).show();
                                                        }

                                                        progressDialog.dismiss();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        progressDialog.dismiss();
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            progressDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void DescargarImagen(String UI) {
        try {
            progressDialog.setMessage("Descargando foto de perfil...");
            progressDialog.show();
            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            mountainsRef = storageRef.child("fotos/" + UI + ".jpg");
            final File localFile = File.createTempFile(UI, "jpg");
            mountainsRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Foto.setImageURI(Uri.parse(localFile.getPath()));
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Error al descargar foto de peril", Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            Toast.makeText(getContext(), "No pudimos descargar tu foto de perfil", Toast.LENGTH_LONG).show();
        }
    }
}

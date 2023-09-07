package com.jica.dangam;

import java.util.ArrayList;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainFragment extends Fragment {
	RecyclerView recyclerView;
	PostProfileAdapter adapter;
	LinearLayoutManager linearLayoutManager;
	ArrayList<PostProfile> list;
	FirebaseFirestore db = FirebaseFirestore.getInstance();

	// 테스트용 임시 로그아웃 버튼
	private Button btnGoogleLogout;
	private GoogleSignInClient mGoogleSignInClient;
	private FirebaseAuth mAuth;

	// MyPageFragment
	private MyPageFragment myPageFragment;
	private FragmentManager fragmentManager;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
		@Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		Toolbar tb = view.findViewById(R.id.mainToolbar);
		((AppCompatActivity)getActivity()).setSupportActionBar(tb);

		recyclerView = view.findViewById(R.id.main_recyclerview);
		linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
		recyclerView.setLayoutManager(linearLayoutManager);
		list = new ArrayList<>();
		adapter = new PostProfileAdapter(list);

		db.collection("post_gam").orderBy("pdate")
			.get()
			.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
				@Override
				public void onComplete(@NonNull Task<QuerySnapshot> task) {
					if (task.isSuccessful()) {
						for (QueryDocumentSnapshot document : task.getResult()) {
							PostProfile profile = document.toObject(PostProfile.class);
							adapter.addItem(profile);
							Log.d("firestore", document.getId() + " => " + document.getData());
							Log.d("object test", profile.getTitle() + " " + profile.getContents());
						}
						adapter.notifyDataSetChanged();
					} else {
						Log.d("firestore", "Error getting documents: ", task.getException());
					}
				}
			});
		recyclerView.setAdapter(adapter);

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestIdToken(getString(R.string.default_web_client_id))
			.requestEmail()
			.build();

		mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
		mAuth = FirebaseAuth.getInstance();

		btnGoogleLogout = view.findViewById(R.id.btnGoogleLogout);
		btnGoogleLogout.setOnClickListener(view1 -> {
			signOut();
		});

		fragmentManager = getChildFragmentManager();

		return view;
	}

	private void signOut() {
		mAuth.signOut();

		mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
			@Override
			public void onComplete(@NonNull Task<Void> task) {
				FirebaseUser user = mAuth.getCurrentUser();

				if (user == null) {
					startActivity(new Intent(getContext(), LoginActivity.class));
					Toast.makeText(getContext(), "Logout", Toast.LENGTH_SHORT).show();
					getActivity().finish();
				}
			}
		});
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main_toolbar, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
}
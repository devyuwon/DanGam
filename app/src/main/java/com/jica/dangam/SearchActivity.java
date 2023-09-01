package com.jica.dangam;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SearchActivity extends AppCompatActivity {
	SearchView search;
	SearchHistoryFragment searchHistoryFG;
	SearchListFragment searchListFG;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//UI layout전개
		setContentView(R.layout.activity_search);

		//UI객체 찿기
		search = findViewById(R.id.search);

		Toolbar toolbar = (Toolbar)findViewById(R.id.searchToolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		BottomNavigationView btNavi = findViewById(R.id.bottomNavigationView_search);

		btNavi.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
			@Override
			public boolean onNavigationItemSelected(@NonNull MenuItem item) {
				if (item.getItemId() == R.id.menuHome) {
					finish();
					return true;
				}
				return false;
			}
		});

		//Fragment 생성
		searchHistoryFG = new SearchHistoryFragment();
		searchListFG = new SearchListFragment();
		FrameLayout layoutContainer = findViewById(R.id.container);

		//FragmentManager,FragmentTransatcion객체 생성
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		//Fragment 찿기
		//Fragment fragment = manager.findFragmentById(R.id.container);

		//처음에는 FrameLayout에 Fragement가 없으므로 Fragment를 추가한다.
		//FragmentTrasaction객체에 CounterFragment를 추가한다.
		transaction.add(R.id.container, searchHistoryFG, "History");
		//최종적으로 commit()메서드 적용하면 Fragment를 추가한다.
		transaction.commit();

		//SerchView에 이벤트 핸들러 설정
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				if (s != null) {
					Bundle bundle = new Bundle(1);
					bundle.putString("SearchWord", s);
					searchListFG.setArguments(bundle);
					FragmentTransaction transaction = manager.beginTransaction();

					Log.d("TAG", "SearchListFG로 교체");
					transaction.replace(R.id.container, searchListFG, "List");
					transaction.commit();
				}

				return false;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}
		});



	}
	public boolean onOptionItemSelected(MenuItem item){
		if (item.getItemId()==android.R.id.home){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
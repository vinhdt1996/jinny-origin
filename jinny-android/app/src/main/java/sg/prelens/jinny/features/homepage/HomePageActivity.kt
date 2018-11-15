package sg.prelens.jinny.features.homepage

import android.support.design.widget.NavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by vinova on 1/16/18.
 */
class HomePageActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    /*private lateinit var model: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun initAdapter() {
        val glide = Glide.with(this)
        val adapter = UserAdapter(glide) {
            model.retry()
        }
        rvUser.adapter = adapter
        model.users.observe(this, Observer<PagedList<User>> {
            adapter.setList(it)
        })
        model.networkState.observe(this, Observer {
            adapter.setNetworkState(it)
        })
    }

    private fun initSwipeToRefresh() {
        model.refreshState.observe(this, Observer {
            swipeRefresh.isRefreshing = it == NetworkState.LOADING
        })
        swipeRefresh.setOnRefreshListener {
            model.refresh()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

    private fun getViewModel(): UserViewModel =
            ViewModelProviders.of(this, object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val repo = ServiceLocator.instance(this@HomePageActivity)
                            .getUserRepository()
                    @Suppress("UNCHECKED_CAST")
                    return UserViewModel(repo) as T
                }

            })[UserViewModel::class.java]*/
}
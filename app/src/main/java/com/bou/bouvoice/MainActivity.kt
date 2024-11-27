package com.bou.bouvoice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bou.bouvoice.databinding.ActivityMainBinding
import com.bou.bouvoice.ui.vote.VoteInstructionsDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.ActionBarDrawerToggle
import com.bou.bouvoice.ui.initial.LoginOverviewActivity
import com.bou.bouvoice.ui.menuItem.EditDetailsActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navController: NavController
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // View binding setup
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize drawerLayout immediately after setting the content view
        drawerLayout = findViewById(R.id.drawer_layout)

        // Set up custom toolbar
        val toolbar: Toolbar = findViewById(R.id.custom_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null // Remove default title

        // Initialize NavController
        navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Set up ActionBarDrawerToggle to control the drawer icon
        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Set up BottomNavigationView
        val bottomNavView: BottomNavigationView = binding.navView
        bottomNavView.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
        setupBottomNavigationView(bottomNavView)

        // Log app startup
        Log.d("BOU Voice", "App started")

        // Set up drawer navigation
        val drawerNavView: NavigationView = findViewById(R.id.nav_view_drawer)
        setupDrawerNavigation(drawerNavView)

        // Load user data into the navigation header
        loadUserDataIntoNavHeader(drawerNavView)

        // Define fragments where BottomNavigationView should be hidden
        val hideBottomNavFragments = setOf(
            R.id.leadershipUpdatesFragment,
            R.id.prioritiesFragment,
            R.id.implementationsFragment,
            R.id.navigation_suggest,
            R.id.printReportFragment,
            R.id.electionSetupFragment,
            R.id.electionSuccessFragment,
            R.id.nomineeListFragment,
            R.id.specificPeopleFragment,
            R.id.finalVotingPageFragment,
            R.id.choosePeopleFragment,
            R.id.votingDetailsFragment,
            R.id.successFragment,
            R.id.resolvedFragment,
            R.id.respondFragment,
            R.id.accountsFragment,
            R.id.recycleBinFragment,
            R.id.departmentDetailsFragment

        )

        // Control BottomNavigationView visibility based on navigation destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNavView.visibility = if (destination.id in hideBottomNavFragments) View.GONE else View.VISIBLE
        }

        // Set up Vote button click listener in the toolbar
        val voteButton: ImageView = findViewById(R.id.vote_button)
        voteButton.setOnClickListener {
            VoteInstructionsDialog(this).showDialog() // Show vote instructions dialog
        }
    }

    private fun loadUserDataIntoNavHeader(drawerNavView: NavigationView) {
        val headerView = drawerNavView.getHeaderView(0)
        val fullNameTextView = headerView.findViewById<TextView>(R.id.user_full_name)
        val departmentTextView = headerView.findViewById<TextView>(R.id.user_department)
        val divisionTextView = headerView.findViewById<TextView>(R.id.user_division)
        val adminRoleTextView = headerView.findViewById<TextView>(R.id.user_admin_role)
        val superAdminRoleTextView = headerView.findViewById<TextView>(R.id.user_super_admin_role)
        val editDetailsButton = headerView.findViewById<TextView>(R.id.edit_details)

        val currentUserEmail = auth.currentUser?.email

        if (currentUserEmail != null) {
            firestore.collection("users").document(currentUserEmail)
                .addSnapshotListener { documentSnapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreError", "Failed to load user info: ${error.message}")
                        return@addSnapshotListener
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        fullNameTextView.text = documentSnapshot.getString("fullName") ?: "Full Name"
                        departmentTextView.text = documentSnapshot.getString("department") ?: "Department"
                        divisionTextView.text = documentSnapshot.getString("division") ?: "Division"
                        adminRoleTextView.text = "Admin: ${documentSnapshot.getString("adminRole") ?: "None"}"
                        superAdminRoleTextView.text = "Super Admin: ${if (documentSnapshot.getBoolean("superAdminRole") == true) "Yes" else "No"}"
                    } else {
                        Log.d("FirestoreInfo", "No such document")
                    }
                }

            // Navigate to Edit Details screen on clicking "Edit Details"
            editDetailsButton.setOnClickListener {
                startActivity(Intent(this, EditDetailsActivity::class.java))
            }
        }
    }

    private fun setupBottomNavigationView(bottomNavView: BottomNavigationView) {
        bottomNavView.setupWithNavController(navController)
        bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_suggest -> {
                    navController.navigate(R.id.welcomeSuggestFragment)
                    true
                }
                R.id.navigation_manage -> {
                    navController.navigate(R.id.navigation_manage)
                    true
                }
                R.id.navigation_bank_wide -> {
                    navController.navigate(R.id.navigation_bank_wide)
                    true
                }
                R.id.navigation_department -> {
                    navController.navigate(R.id.navigation_department)
                    true
                }
                R.id.navigation_feedback -> {
                    navController.navigate(R.id.navigation_feedback)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDrawerNavigation(drawerNavView: NavigationView) {
        drawerNavView.setupWithNavController(navController)
        drawerNavView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_vote -> {
                    navController.navigate(R.id.action_menu_vote_to_electionSetupFragment)
                    Log.d("Drawer", "Vote selected - Navigating to Election Setup")
                }
                R.id.nav_resolutions -> {
                    navController.navigate(R.id.implementationsFragment)
                    Log.d("Drawer", "Resolutions selected - Navigating to Implementations")
                }
                R.id.nav_leadership_updates -> {
                    navController.navigate(R.id.leadershipUpdatesFragment)
                    Log.d("Drawer", "Leadership Updates selected - Navigating to Leadership Updates")
                }
                R.id.nav_priorities -> {
                    navController.navigate(R.id.prioritiesFragment)
                    Log.d("Drawer", "Priorities selected - Navigating to Priorities")
                }
                R.id.nav_recycle_bin -> {
                    navController.navigate(R.id.recycleBinFragment)
                    Log.d("Drawer", "Recycle Bin selected - Navigating to Recycle Bin")
                }
                R.id.nav_print_report -> {
                    navController.navigate(R.id.printReportFragment)
                    Log.d("Drawer", "Print Report selected - Navigating to Print Report")
                }
                R.id.nav_logout -> {
                    Log.d("Drawer", "Logout selected")
                    logoutUser()
                }
                else -> {}
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (!navController.popBackStack()) {
            super.onBackPressed()
        }
    }

    private fun logoutUser() {
        auth.signOut()
        Log.d("BOU Voice", "User logged out")
        val intent = Intent(this, LoginOverviewActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

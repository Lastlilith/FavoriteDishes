package com.example.favoritedishes.view.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.favoritedishes.R
import com.example.favoritedishes.application.FavDishApplication
import com.example.favoritedishes.databinding.DialogCustomListBinding
import com.example.favoritedishes.databinding.FragmentAllDishesBinding
import com.example.favoritedishes.model.entities.FavDish
import com.example.favoritedishes.utils.Constants
import com.example.favoritedishes.view.activities.AddUpdateDishActivity
import com.example.favoritedishes.view.activities.MainActivity
import com.example.favoritedishes.view.adapters.CustomListItemAdapter
import com.example.favoritedishes.view.adapters.FavDishAdapter
import com.example.favoritedishes.viewmodel.FavDishViewModel
import com.example.favoritedishes.viewmodel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding

    /**
     * To create the ViewModel we used the viewModels delegate, passing in an instance of our FavDishViewModelFactory.
     * This is constructed based on the repository retrieved from the FavDishApplication.
     */
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    // TODO Step 1: Make the global variable for FavDishAdapter class as below instead of local.
    // START
    // A global variable for FavDishAdapter Class
    private lateinit var mFavDishAdapter: FavDishAdapter
    // END

    // TODO Step 3: Make the CustomItemsListDialog as global instead of local as below.
    // START
    private lateinit var mCustomListDialog: Dialog
    // END

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the LayoutManager that this RecyclerView will use.
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        // Adapter class is initialized and list is passed in the param.
        // TODO Step 2: Make this variable as global
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        // adapter instance is set to the recyclerview to inflate the items.
        mBinding.rvDishesList.adapter = mFavDishAdapter

        /**
         * Add an observer on the LiveData returned by getAllDishesList.
         * The onChanged() method fires when the observed data changes and the activity is in the foreground.
         */
        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                if (it.isNotEmpty()) {

                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE

                    mFavDishAdapter.dishesList(it)
                } else {

                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    /**
     * A function to navigate to the Dish Details Fragment.
     *
     * @param favDish
     */
    fun dishDetails(favDish: FavDish) {

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }

        findNavController()
            .navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }

            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Method is used to show the Alert Dialog while deleting the dish details.
     *
     * @param dish - Dish details that we want to delete.
     */
    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        //set message for alert dialog
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A function to launch the custom dialog.
     */
    private fun filterDishesListDialog() {
        // TODO Step 4: Make this variable as global.
        mCustomListDialog = Dialog(requireActivity())

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)

        // Set the LayoutManager that this RecyclerView will use.
        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        // Adapter class is initialized and list is passed in the param.
        // TODO Step 8: Pass the required param as below.
        // START
        val adapter = CustomListItemAdapter(
            requireActivity(),
            this@AllDishesFragment,
            dishTypes,
            Constants.FILTER_SELECTION
        )
        // END
        // adapter instance is set to the recyclerview to inflate the items.
        binding.rvList.adapter = adapter
        //Start the dialog and display it on screen.
        mCustomListDialog.show()
    }


    // TODO Step 5: Create a function to get the filter item selection and get the list from database accordingly.
    // START
    /**
     * A function to get the filter item selection and get the list from database accordingly.
     *
     * @param filterItemSelection
     */
    fun filterSelection(filterItemSelection: String) {

        mCustomListDialog.dismiss()

        Log.i("Filter Selection", filterItemSelection)

        if (filterItemSelection == Constants.ALL_ITEMS) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {

                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    } else {

                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){
                dishes ->
                dishes.let {
                    if(it.isNotEmpty()){
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    } else {
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
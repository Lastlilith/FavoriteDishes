package com.example.favoritedishes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.favoritedishes.application.FavDishApplication
import com.example.favoritedishes.databinding.FragmentFavoriteDishesBinding
import com.example.favoritedishes.model.entities.FavDish
import com.example.favoritedishes.view.activities.MainActivity
import com.example.favoritedishes.view.adapters.FavDishAdapter
import com.example.favoritedishes.viewmodel.DashboardViewModel
import com.example.favoritedishes.viewmodel.FavDishViewModel
import com.example.favoritedishes.viewmodel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var mBinding: FragmentFavoriteDishesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = mBinding!!

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFavDishViewModel.favoriteDishes.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                mBinding!!.rvFavoriteDishesList.layoutManager =
                    GridLayoutManager(requireActivity(), 2)
                val adapter = FavDishAdapter(this)
                mBinding!!.rvFavoriteDishesList.adapter = adapter

                if (it.isNotEmpty()) {
                    mBinding!!.rvFavoriteDishesList.visibility = View.VISIBLE
                    mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.GONE
                    adapter.dishesList(it)
                } else {
                    mBinding!!.rvFavoriteDishesList.visibility = View.GONE
                    mBinding!!.tvNoFavoriteDishesAvailable.visibility = View.VISIBLE
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish) {
        findNavController().navigate(
            FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(
                favDish
            )
        )

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }

    }

    override fun onResume() {
        super.onResume()
        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}
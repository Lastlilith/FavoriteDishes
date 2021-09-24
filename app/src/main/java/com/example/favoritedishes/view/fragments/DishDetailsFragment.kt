package com.example.favoritedishes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.favoritedishes.R
import com.example.favoritedishes.databinding.FragmentDishDetailsBinding
import java.io.IOException
import java.util.*

class DishDetailsFragment : Fragment() {
    private var mBidning:FragmentDishDetailsBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBidning = FragmentDishDetailsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return mBidning!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: DishDetailsFragmentArgs by navArgs()

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .into(mBidning!!.ivDishImage)
            } catch (e: IOException){
                e.printStackTrace()
            }

            mBidning!!.tvTitle.text = it.dishDetails.title
            mBidning!!.tvType.text = it.dishDetails.type.capitalize(Locale.ROOT)// to make first letter capital
            mBidning!!.tvCategory.text = it.dishDetails.category
            mBidning!!.tvIngredients.text = it.dishDetails.ingredients
            mBidning!!.tvCookingTime.text =
                resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)
            mBidning!!.tvCookingDirection.text = it.dishDetails.directionToCook
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBidning = null
    }
}
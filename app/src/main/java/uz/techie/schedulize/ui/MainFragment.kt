package uz.techie.schedulize.ui

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import uz.techie.schedulize.R
import uz.techie.schedulize.adapters.ViewPagerAdapter
import uz.techie.schedulize.databinding.FragmentMainBinding

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val TAG = MainFragment::class.java.canonicalName

    private val viewModel: MainFragmentViewModel by viewModels()

    private var _binding:FragmentMainBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        toolbar?.apply {
            setTitle(R.string.app_name)
            navigationIcon = null
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater,container,false)

        viewModel.isSubjectDataSourceIsEmptyLiveData.observe(viewLifecycleOwner) { isEmpty ->

            if (isEmpty) {
                binding.textWhenEmpty.visibility = View.VISIBLE
                binding.groupViewpager.visibility = View.GONE
                binding.viewPager.adapter = null

            } else {
                binding.textWhenEmpty.visibility = View.GONE
                binding.groupViewpager.visibility = View.VISIBLE
                binding.viewPager.visibility = View.VISIBLE



                val adapter = ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle)

                viewModel.listOfDayOfWeekFromRepositoryLiveData.observe(viewLifecycleOwner) { listOfDays ->
                    adapter.setListDayOfWeek(listOfDays)

                    viewModel.listNamesOfDayResource.clear()
                    val dayOfWeek = resources.getStringArray(R.array.day_of_week)
                    listOfDays.forEach {
                        viewModel.listNamesOfDayResource.add(dayOfWeek[it])
                    }
                    binding.viewPager.adapter = adapter
                    binding.viewPager.offscreenPageLimit = 2
                    TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                        tab.text =  viewModel.listNamesOfDayResource[position]
                    }.attach()
                }




            }
        }
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_subject -> {
                findNavController().navigate(R.id.action_fragmentMain_to_addFragment)
                true
            }
            R.id.setting -> {
                findNavController().navigate(R.id.action_fragmentMain_to_settingsFragment)
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
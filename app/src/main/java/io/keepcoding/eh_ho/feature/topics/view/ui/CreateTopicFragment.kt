package io.keepcoding.eh_ho.feature.topics.view.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.common.LoadingDialogFragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.domain.CreateTopicModel
import kotlinx.android.synthetic.main.fragment_create_topic.*

const val TAG_LOADING_DIALOG = "Loading_dialog"
const val CREATE_TOPIC_FRAGMENT_TAG = "CREATE_TOPIC_FRAGMENT"

class CreateTopicFragment : Fragment() {

    private var listener: CreateTopicInteractionListener? = null
    private lateinit var loadingDialogFragment: LoadingDialogFragment

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is CreateTopicInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement ${CreateTopicInteractionListener::class.java.simpleName}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        loadingDialogFragment =
            LoadingDialogFragment.newInstance(getString(R.string.label_create_topic))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_topic, container, false)

    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_create_topic, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_send -> listener?.onCreateTopicOptionClicked(
                model = CreateTopicModel(
                    title = inputTitle.text.toString(),
                    content = inputContent.text.toString()
                )
            )
        }
        return super.onOptionsItemSelected(item)
    }

    fun enableLoadingDialog(enable: Boolean) {
        if (enable)
            loadingDialogFragment.show(
                childFragmentManager,
                TAG_LOADING_DIALOG
            )
        else
            loadingDialogFragment.dismiss()
    }


    interface CreateTopicInteractionListener {
        fun onCreateTopicOptionClicked(model: CreateTopicModel)
    }
}
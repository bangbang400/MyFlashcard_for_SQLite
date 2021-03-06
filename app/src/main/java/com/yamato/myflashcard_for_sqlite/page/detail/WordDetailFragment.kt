package com.yamato.myflashcard_for_sqlite.page.detail

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.yamato.myflashcard_for_sqlite.R
import com.yamato.myflashcard_for_sqlite.databinding.WordDetailFragmentBinding
import com.yamato.myflashcard_for_sqlite.model.Word
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WordDetailFragment:Fragment(R.layout.word_detail_fragment), View.OnClickListener {

    private val vm:WordDetailModel by viewModels()
    private var _binding: WordDetailFragmentBinding? = null
    private val binding: WordDetailFragmentBinding get() = _binding!!
    private val args: WordDetailFragmentArgs by navArgs()

    companion object{
        var allcnt = 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this._binding = WordDetailFragmentBinding.bind(view)

        // メニューを画面に表示させる
        setHasOptionsMenu(true)
        // タイトルバー設定
        (activity as AppCompatActivity).supportActionBar?.title = "単語詳細"
        // ツールバーに戻るボタンを設置
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setFragmentResultListener("edit") { _,data ->
            val words = data.getParcelable("word") as? Word ?: return@setFragmentResultListener
            vm.words.value = words
        }

        setFragmentResultListener("confirmItemDelete"){_,data ->
            // DialogInterface.BUTTON_NEGATIVE ・・・キャンセルボタンが押下された
            val which = data.getInt("result",DialogInterface.BUTTON_NEGATIVE)
            if (which == DialogInterface.BUTTON_POSITIVE){
                vm.delete()
                Log.d("TEST","削除ダイアログ、OK")
            }
        }

        if (savedInstanceState == null) {
            vm.words.value = args.words
        }

        // リストのアイテムから受け取った単語の表示
        val words = args.words

        // テキスト：単語
        binding.textViewWordWordDetail.text = words.word
        // テキスト：解説
        binding.textViewCommentaryWordDetail.text = words.commentary


        binding.textViewCommentaryWordDetail.visibility = View.INVISIBLE

        // 解説ボタン
        binding.buttonCommentaryWordDetail.setOnClickListener(this)
        // わかるボタン
        binding.buttonKnowWordDetail.setOnClickListener(this)
        // わからないボタン
        binding.buttonUnknownWordDetail.setOnClickListener(this)

        vm.errorMessage.observe(viewLifecycleOwner) { msg ->
            if (msg.isEmpty()) return@observe
            Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show()
            vm.errorMessage.value = ""
        }
        vm.done.observe(viewLifecycleOwner) {
            // リストへ遷移させる
            findNavController().popBackStack()
        }

        vm.deleted.observe(viewLifecycleOwner){
            // 削除が完了したときの処理
            Toast.makeText(context,"${words.word}を削除しました。",Toast.LENGTH_SHORT).show()
            if (allcnt > 1) {
                // 単語リストに単語がまだある場合、リストに戻る
                findNavController().popBackStack(R.id.wordListFragment,false)
            }else{
                // 単語リストに単語がない場合、メイン画面に戻る
                findNavController().popBackStack(R.id.mainFragment,false)
            }
        }
        vm.allCnt.observe(viewLifecycleOwner){
            allcnt = it
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_item_detail,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().popBackStack()
                true
            }
            R.id.item_edit -> {
                Log.d("TEST","編集ボタンおされた")
                //　単語１件の編集ボタン
                val words = vm.words.value ?: return true
                val action = WordDetailFragmentDirections.actionWordDetailFragmentToWordEditFragment(words)
                findNavController().navigate(action)
                true
            }
            R.id.item_delete -> {
                Log.d("TEST","削除ボタンおされた")
                // 単語１件の削除ボタン
                // detatil to Dialog
                findNavController().navigate(
                    R.id.action_wordDetailFragment_to_wordConfirmItemDialogFragment
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(p0: View?) {
        var judge = false

        if (R.id.button_know_word_detail == p0?.id) {
            // わかるボタン
            judge = true
            addJudgement(judge)
        }else if(R.id.button_unknown_word_detail == p0?.id) {
            // わからないボタン
            judge = false
            addJudgement(judge)
        }else if(R.id.button_commentary_word_detail == p0?.id){
            // 解説ボタン
            // 解説ボタンを押下したらボタンを非表示にする
            binding.buttonCommentaryWordDetail.visibility = View.INVISIBLE
            //　解説ボタンを押下したら解説を表示する
            binding.textViewCommentaryWordDetail.visibility = View.VISIBLE
        }
    }

    private fun addJudgement(judge:Boolean) {

        var correct = 0
        var wrong = 0

        if(judge){
            // わかる
            correct = 1
        }else{
            //　わからない
            wrong = 1
        }
        // 正当数を保存する
        vm.addJudgement(args.words,correct,wrong)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._binding = null
    }
}
package space.alair.potplayer_control.pages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import space.alair.potplayer_control.R
import space.alair.potplayer_control.databinding.ActivityHomeBinding
import space.alair.potplayer_control.presenters.RemotePresenter


class HomeActivity : AppCompatActivity() {
    val remotePresenter by lazy {
        RemotePresenter.instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        remotePresenter.connect()
        val bind = DataBindingUtil.setContentView<ActivityHomeBinding>(this, R.layout.activity_home)
        bind.controller = this
    }

    override fun onDestroy() {
        super.onDestroy()
        remotePresenter.close()
    }
}
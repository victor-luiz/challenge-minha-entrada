package br.com.minhaentrada.victor.challenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.minhaentrada.victor.challenge.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.textView.text = "Hello Word, Minha Entrada!"
    }
}
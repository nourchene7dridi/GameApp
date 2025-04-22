
package com.example.gameapp.MULTIPLAYER.ShakeIt

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.gameapp.R
import java.io.IOException
import java.util.*
import kotlin.math.sqrt

class ShakeItGame : ComponentActivity(), SensorEventListener, AdapterView.OnItemClickListener {
    // Bluetooth components
    private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FA")
    private var ownSocket: BluetoothSocket? = null
    private val REQUEST_ENABLE_BT = 1
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var deviceListAdapter: ArrayAdapter<String>
    private var deviceList = ArrayList<String>()
    private var deviceListAdr = ArrayList<String>()

    // Game components
    private lateinit var timerTextView: TextView
    private lateinit var countDownTimer: CountDownTimer
    private var score: Int = 0
    private var opponentScore: Int = 0
    private lateinit var scoreTextView: TextView
    private lateinit var opponentScoreTextView: TextView
    private lateinit var shakeGif: ImageView

    // Sensor components
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var isGameRunning = false
    private var lastShakeTime = 0L
    private val cooldown = 500 // ms between valid shakes
    private var serveurScore: Int = 0

    // Media players
    private lateinit var mediaPlayerShaker: MediaPlayer
    private lateinit var mediaPlayerWin: MediaPlayer
    private lateinit var mediaPlayerLose: MediaPlayer
    private lateinit var mediaPlayerDing: MediaPlayer

    // Multiplayer states
    private var isServer = false
    private var isClient = false

    companion object {
        const val NAME = "ShakeItBluetoothServer"
        private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when (action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address

                    deviceList.add("${deviceName}\n${device?.address}")
                    if (deviceHardwareAddress != null) {
                        deviceListAdr.add(deviceHardwareAddress)
                    }
                    deviceListAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.multiplayer_shake_it)

        // Initialize views
        timerTextView = findViewById(R.id.time)
        scoreTextView = findViewById(R.id.score)
        opponentScoreTextView = findViewById(R.id.scoreADV)
        shakeGif = findViewById(R.id.shakeGif)

        // Initialize media players
        mediaPlayerShaker = MediaPlayer.create(this, R.raw.effet_shaker)
        mediaPlayerShaker.isLooping = true
        mediaPlayerWin = MediaPlayer.create(this, R.raw.bonne_reponse)
        mediaPlayerLose = MediaPlayer.create(this, R.raw.mauvaise_reponse)
        mediaPlayerDing = MediaPlayer.create(this, R.raw.fin)

        // Initialize sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        // Initialize Bluetooth
        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        // Register for broadcasts when a device is discovered
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        showBLEconnect()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showBLEconnect() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.popup_invite_bluetooth, null)
        //val titleTextView = dialogView.findViewById<TextView>(R.id.text_title)
        //val messageTextView = dialogView.findViewById<TextView>(R.id.text_message)
        val inviteButton = dialogView.findViewById<Button>(R.id.invite)
        val waitButton = dialogView.findViewById<Button>(R.id.attente)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        deviceList = ArrayList()
        deviceListAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, deviceList)

        bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter

        inviteButton.setOnClickListener {// on recherche un server, c'est le client
            val listView = findViewById<ListView>(R.id.deviceListView)
            listView.visibility = View.VISIBLE
            listView.adapter = deviceListAdapter
            listView.onItemClickListener = this

            if (bluetoothAdapter == null){
                //Toast.makeText(this, "Impossible d'utiliser le bluetooth", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Connection est possible", Toast.LENGTH_SHORT).show()
                //val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
                bluetoothAdapter = bluetoothManager.adapter
                if (bluetoothAdapter == null){
                    Toast.makeText(this, "Impossible d'utiliser le bluetooth", Toast.LENGTH_SHORT).show()
                } else {
                    if(!bluetoothAdapter.isEnabled){
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            val REQUEST_CODE_BLUETOOTH_PERMISSION = 1001
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                REQUEST_CODE_BLUETOOTH_PERMISSION
                            )
                        }


                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                        searchBluetoothDevices()
                    }else{ //Si le Bluetooth est déja activé
                        searchBluetoothDevices()
                    }
                }
            }
            dialog.dismiss()
        }

        //on attend une connexion (c'est le serveur)
        waitButton.setOnClickListener {
            val progress = dialogView.findViewById<View>(R.id.progressBar)
            progress.visibility = View.VISIBLE

            if (bluetoothAdapter == null){
                //Toast.makeText(this, "Impossible d'utiliser le bluetooth", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Connection est possible", Toast.LENGTH_SHORT).show()
                //val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
                bluetoothAdapter = bluetoothManager.adapter
                if (bluetoothAdapter == null){
                    Toast.makeText(this, "Impossible d'utiliser le bluetooth", Toast.LENGTH_SHORT).show()
                } else {
                    if(!bluetoothAdapter.isEnabled){
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                Manifest.permission.BLUETOOTH_CONNECT
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            val REQUEST_CODE_BLUETOOTH_PERMISSION = 1001
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                                REQUEST_CODE_BLUETOOTH_PERMISSION
                            )
                        }


                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
                        //searchBluetoothDevices()
                        val accepted = AcceptThread(dialog)
                        accepted.start()
                    }else{ //Si le Bluetooth est déja activé
                        //searchBluetoothDevices()
                        val accepted = AcceptThread(dialog)
                        accepted.start()
                    }
                }
            }
            //Toast.makeText(this, "En attente d'un ami", Toast.LENGTH_SHORT).show()
            //val accepted = AcceptThread(dialog)
            //accepted.start()
            //dialog.dismiss()
        }

        dialog.show()
    }

    private fun checkBluetoothPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_ENABLE_BT
            )
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // Le Bluetooth a été activé avec succès
                //Toast.makeText(this, "Bluetooth activé avec succès", Toast.LENGTH_SHORT).show()
                // Ajoutez ici le code à exécuter lorsque le Bluetooth est activé
                searchBluetoothDevices()

            } else {
                // L'utilisateur a annulé ou refusé d'activer le Bluetooth
                Toast.makeText(this, "Activation du Bluetooth annulée", Toast.LENGTH_SHORT).show()
                // Ajoutez ici le code à exécuter lorsque l'activation du Bluetooth est annulée
                val intent = Intent(applicationContext, ShakeItGame::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun searchBluetoothDevices() {
        deviceList.clear()
        deviceListAdapter.notifyDataSetChanged()

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices

        pairedDevices?.forEach { device ->
            deviceList.add("${device.name}\n${device.address}")
            deviceListAdr.add(device.address)
        }

        // Démarrez la découverte de nouveaux périphériques
        bluetoothAdapter.startDiscovery()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedDevice = deviceListAdr[position]
        val device: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(selectedDevice)
        val socket = device?.createRfcommSocketToServiceRecord(MY_UUID)

        val connectThread = ConnectThread(socket)
        if (socket != null) {
            ownSocket = socket
        }
        isClient = true

        val progressBar = findViewById<View>(R.id.progressBar3)
        progressBar.visibility = View.VISIBLE
        connectThread.start()
    }

    private fun startGame() {
        isGameRunning = true
        score = 0
        opponentScore = 0
        scoreTextView.text = "MOIhbjhbjk : $score"
        opponentScoreTextView.text = "ADV : $opponentScore"

        startCountDownTimer()
        // Load shaker GIF
        Glide.with(this).asGif().load(R.drawable.shaker).into(shakeGif)

        // Start shaker sound
        mediaPlayerShaker.start()
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                timerTextView.text = "$secondsLeft s"
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun endGame() {
        isGameRunning = false
        mediaPlayerShaker.pause()
        mediaPlayerDing.start()

        showGameOverDialog()
    }

    private fun showGameOverDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.overonlinegame1, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.result)
        val quitButton = dialogView.findViewById<Button>(R.id.quitter)
        val resumeButton = dialogView.findViewById<Button>(R.id.replay)

        val resultText = when {
            isServer && score > opponentScore -> {
                mediaPlayerWin.start()
                "😏 Victoire 😎"
            }
            isServer && score < opponentScore -> {
                mediaPlayerLose.start()
                "😪 Défaite 😭"
            }
            isClient && score > opponentScore -> {
                mediaPlayerWin.start()
                "😏 Victoire 😎"
            }
            isClient && score < opponentScore -> {
                mediaPlayerLose.start()
                "😪 Défaite 😭"
            }
            else -> {
                mediaPlayerDing.start()
                "😪 Match Nul 😭"
            }
        }

        messageTextView.text = resultText

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        quitButton.setOnClickListener {
            dialog.dismiss()
            finish()
        }

        resumeButton.setOnClickListener {
            dialog.dismiss()
            startGame()
        }

        dialog.show()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isGameRunning || event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z) - SensorManager.GRAVITY_EARTH

        if (acceleration > 5) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastShakeTime > cooldown) {
                lastShakeTime = currentTime
                score++
                scoreTextView.text = "MOIHHJBJHKBK : $score"

                // Send score to opponent
                //sendMessage("SCORE:$score")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onResume() {
        super.onResume()
        accelerometer?.also { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        mediaPlayerShaker.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
        mediaPlayerShaker.release()
        mediaPlayerWin.release()
        mediaPlayerLose.release()
        mediaPlayerDing.release()
        ownSocket?.close()
        countDownTimer.cancel()
    }

    @SuppressLint("MissingPermission")
    private inner class AcceptThread(private val dialog: AlertDialog?) : Thread() {

        private val mmServerSocket: BluetoothServerSocket? by lazy(LazyThreadSafetyMode.NONE) {
            bluetoothAdapter?.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID)
        }

        override fun run() {
            // Keep listening until exception occurs or a socket is returned.
            println("Demarrage du serveur")
            isServer = true
            var shouldLoop = true
            //var socket: BluetoothSocket?
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                }

                catch (e: IOException) {
                    Log.e(TAG, "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    ownSocket = socket
                    manageCommunicationServer(it)
                    //mmServerSocket?.close()
                    //shouldLoop = false
                }
            }
        }

        fun sendMessage( message: String){
            try {
                val outputStream = ownSocket?.outputStream
                outputStream?.write(message.toByteArray())
            } catch (e: IOException) {
                // Gérer l'exception
                println(e)
            }
        }

        fun manageCommunicationServer(socket: BluetoothSocket) {
            try {
                while(true){
                    // Lire les données reçues du périphérique connecté
                    val inputStream = socket.inputStream
                    val availableBytes = inputStream.available()

                    if (availableBytes > 0) {
                        val buffer = ByteArray(1024)
                        val bytesRead = inputStream.read(buffer)
                        val receivedMessage = String(buffer, 0, bytesRead)
                        Log.d(TAG, "Message reçu : $receivedMessage")

                        val outputStream = socket.outputStream

                        when (receivedMessage) {
                            "Start Game" -> {
                                println("Game is starting")
                                dialog?.dismiss()
                                startGame()
                                val response = "Ok_Start_Game"
                                outputStream.write(response.toByteArray())
                                Log.d(TAG, "Reponse serveur : $response")
                                //println("cc")

                            }
                            "2" -> {
                                println("x is 2")
                            }
                            "3" -> {
                                println("x is 3")
                            }
                            else -> println(receivedMessage)
                        }
                        if(receivedMessage.startsWith("ADV")){
                            val scoreAdvTextView = findViewById<TextView>(R.id.scoreADV)
                            opponentScore= receivedMessage.substringAfter("ADV : ").toInt()
                            scoreAdvTextView.text = "ADV : $opponentScore"
                        }
                    }
                }


                // Envoyer une réponse au périphérique connecté



                // Fermer le socket après avoir terminé la communication
                //println("Fin de communication")
                //socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Erreur lors de la gestion de la connexion", e)
            }
        }

        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }

    }

    private inner class ConnectThread(private val socket: BluetoothSocket?) : Thread() {

        override fun run() {
            println("We are here")
            try {
                // Tentez de connecter le socket
                socket?.connect()
                val listView = findViewById<ListView>(R.id.deviceListView)
                listView.visibility = View.INVISIBLE

                val scrollbar = findViewById<View>(R.id.progressBar3)
                scrollbar.visibility = View.INVISIBLE

                // La connexion est établie avec succès, vous pouvez maintenant communiquer avec le périphérique
                // Par exemple, envoyer et recevoir des données
                println("Succes de la connexion")
                isClient = true

                //Envoyer un message
                /*val outputStream = socket?.outputStream

                // Écrire le message à envoyer sur le flux de sortie
                val message = "Connecté"
                outputStream?.write(message.toByteArray())
                println("Succes de la connexion")*/
                sendMessage("Start Game")
                while (true){
                    listenForMessages() // foctionne un message
                }

            } catch (e: IOException) {
                // Une exception s'est produite lors de la tentative de connexion
                // Gérer cette situation en conséquence
                println(e)
            }
        }

        fun sendMessage(message: String) {
            try {
                val outputStream = socket?.outputStream
                outputStream?.write(message.toByteArray())
            } catch (e: IOException) {
                // Gérer l'exception
                println(e)
            }
        }

        // Méthode pour écouter en continu pour les nouveaux messages
        private fun listenForMessages() {
            try {
                val inputStream = socket?.inputStream
                val availableBytes = inputStream?.available()

                if (availableBytes != null) {
                    if (availableBytes > 0) {
                        val buffer = ByteArray(1024)
                        var bytes: Int

                        // Boucle pour lire continuellement à partir du flux d'entrée
                        while (true) {
                            bytes = inputStream.read(buffer) ?: break
                            val message = String(buffer, 0, bytes)

                            // Traiter le message reçu
                            println("Message reçu: $message")
                            if (message == "Ok_Start_Game"){
                                startGame()
                            }
                            if(message.startsWith("ADV : ")){
                                val scoreAdvTextView = findViewById<TextView>(R.id.scoreADV)
                                serveurScore= message.substringAfter("ADV : ").toInt()
                                scoreAdvTextView.text = "ADV : ${serveurScore}"
                            }

                        }
                    }
                }
            } catch (e: IOException) {
                // Gérer l'exception
                println(e)
            }
        }

        // Méthode pour annuler la connexion
        fun cancel() {
            try {
                socket?.close()
            } catch (e: IOException) {
                // Gérer l'exception
            }
        }
    }
}

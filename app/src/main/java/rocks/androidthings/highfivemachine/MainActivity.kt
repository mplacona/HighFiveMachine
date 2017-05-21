package rocks.androidthings.highfivemachine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.things.contrib.driver.button.Button
import com.google.android.things.contrib.driver.pwmservo.Servo
import com.google.firebase.database.*


class MainActivity : AppCompatActivity() {
    private var mDatabaseRef: DatabaseReference? = null
    private var mButton: Button? = null
    private var mServo: Servo? = null
    private val TAG = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDatabaseRef = FirebaseDatabase.getInstance().reference.child("new-follower")

        mButton = Button("BCM6", Button.LogicState.PRESSED_WHEN_LOW)
        mButton!!.setOnButtonEventListener { _, _ ->
            mDatabaseRef?.push()?.setValue("whaddup")
        }

        mServo = Servo("PWM0")
        mServo?.setPulseDurationRange(1.0, 2.0)
        mServo?.setAngleRange((-90).toDouble(), 90.0)
        mServo?.setEnabled(true)

        mDatabaseRef?.addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError?) {
                Log.d(TAG, "Cancelled")
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                Log.d(TAG, "Child Moved")
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                Log.d(TAG, "Child Changed")
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, prevChildKey: String?) {
                Log.d(TAG, "New child added" + dataSnapshot?.key)
                highFive()

                // Remove the item once user has been high-fived
                mDatabaseRef?.child(dataSnapshot?.key)?.removeValue()
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                Log.d(TAG, "Child Removed")
            }

        })
    }

    fun highFive(){
        mServo?.angle = mServo?.maximumAngle!! //up
        Thread.sleep(3000)
        mServo?.angle = mServo?.minimumAngle!! //down
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mButton != null){
            mButton?.close()
        }

        if(mServo != null){
            mServo?.close()
        }
    }
}

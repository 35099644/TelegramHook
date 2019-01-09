// TelegramListener.aidl
package com.telegram.hook;

// Declare any non-default types here with import statements
import com.telegram.hook.PeifengController;
interface TelegramListener {
   void currentStatus(int status);
   void registerController(PeifengController controller);
}

# Android HCE F Demo

Minimal code demo for setting up HCE-F in Android

# Limitations

Android HCE-F has the following limitations:
* PMM value cannot be configured dynamically, it has to be configured through XML declaration;
* NFCID2 value must be specified in range from "02FE000000000000" to "02FEFFFFFFFFFFFF";
* System code can only be specified in range from "4000" to "4FFF" (excluding "4*FF");
  * This limitation is artificial, can be bypassed with ROOT, and was supposedly added to prevent unauthorized emulation of existing FeliCa services;
  * "FF" value cannot be used in a byte, as it indicates a wildcard match for any value in that byte.
* Emulation can only happen when related Activity is on-screen, screen-off, background modes are not supported;
* One service can only handle a single system code;

# References

* [Android Developers - NfcFCardEmulation](https://developer.android.com/reference/android/nfc/cardemulation/NfcFCardEmulation);
* [Android Developers - HostNfcFService](https://developer.android.com/reference/android/nfc/cardemulation/HostNfcFService).

# POuL B.I.T.S. client for Android

With home screen widget

## How to build

- Import in Android Studio
- Click "Fix", "Okay", "Yeah bitch shut up" or similar until Android Studio is happy
- Click Build

## Build flavors

### Libre

The default. Includes only free/libre libraries. Does not support live updates.

### Internal

Same as libre, with an additional MQTT update service implementation that can be
used inside the headquarters' LAN to get realtime notifications.

### Nonlibre

Currently a stub, it's the same as libre. In the future a Firebase Cloud Messaging
update service will be added to get realtime updates outside of the headquarters,
without draining the battery.

## Contributing

You can contribute by sending pull requests to this repo. If you'd like to donate
money you can come to [our headquarters](https://www.openstreetmap.org/#map=19/45.47706/9.22970)
and grab a coffee, or bring us hardware ;)

## License

The code is licensed
under the GPL v3 license.

Most included libraries are licensed under the Apache 2.0 license, except for
Eclipse Paho which is licensed under Eclipse Distribution License.

See the in-app about screen for more information.

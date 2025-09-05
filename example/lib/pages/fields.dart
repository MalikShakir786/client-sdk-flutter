import 'package:dart_jsonwebtoken/dart_jsonwebtoken.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:livekit_example/pages/connect.dart';

import '../widgets/text_field.dart';

class FieldsPage extends StatefulWidget {
  FieldsPage({super.key});

  @override
  State<FieldsPage> createState() => _FieldsPageState();
}

class _FieldsPageState extends State<FieldsPage> {

  final _nameCtrl = TextEditingController();

  final _roomCtrl = TextEditingController();

  String generateLiveKitToken({
    required String apiKey,
    required String apiSecret,
    required String roomName,
    required String identity,
    Duration validFor = const Duration(hours: 1),
  }) {
    final now = DateTime.now();
    final exp = now.add(validFor);

    final jwt = JWT(
      {
        'video': {
          'room': roomName,
          'roomJoin': true,
          'canPublish': true,
          'canSubscribe': true,
        },
      },
      subject: identity,
      issuer: apiKey,
    );

    final token = jwt.sign(SecretKey(apiSecret));
    return token;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        alignment: Alignment.center,
        child: SingleChildScrollView(
          child: Container(
            padding: const EdgeInsets.symmetric(
              horizontal: 20,
              vertical: 20,
            ),
            constraints: const BoxConstraints(maxWidth: 400),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Padding(
                  padding: const EdgeInsets.only(bottom: 70),
                  child: SvgPicture.asset(
                    'images/logo-dark.svg',
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(bottom: 25),
                  child: LKTextField(
                    label: 'User Identity',
                    ctrl: _nameCtrl,
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.only(bottom: 25),
                  child: LKTextField(
                    label: 'Room Name',
                    ctrl: _roomCtrl,
                  ),
                ),
                ElevatedButton(
                  onPressed: (){
                    if(_nameCtrl.text.isNotEmpty && _roomCtrl.text.isNotEmpty){
                      String token = generateLiveKitToken(
                          apiKey: 'API7LuSb9BMrQCv',
                          apiSecret: 'PyhtEBeJVxMIzlPneAvQfiQ0zSbZghH70dhgNMMj7ZcA',
                          roomName: _roomCtrl.text,
                          identity: _nameCtrl.text);
                      print('Token $token');
                      if(token.isNotEmpty){
                        Navigator.push(context, MaterialPageRoute(builder: (context){
                          return ConnectPage(token: token);
                        }));
                      }
                    }
                  },
                  child: const Text('Generate Token'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

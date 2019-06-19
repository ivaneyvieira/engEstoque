window.app_ui_Beeper = function() {
    var audioCtx = new (window.AudioContext || window.webkitAudioContext || window.audioContext);
    this.beep = function(duration, frequency) {
        var oscillator = audioCtx.createOscillator();
        var gainNode = audioCtx.createGain();
        oscillator.connect(gainNode);
        gainNode.connect(audioCtx.destination);
        if (frequency){oscillator.frequency.value = frequency;}
        oscillator.start();
        setTimeout(function(){oscillator.stop()}, (duration ? duration : 500));
    };
};
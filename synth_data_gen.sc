s.boot;

(
SynthDef(\synth, { |out=0, atk=0.01, dcy=2, rls=1, cutoff=1000, lfoRate| var lfo, x;

	lfo = SinOsc.kr(freq: lfoRate).range(0, 20000);
	x = MoogFF.ar(Pulse.ar(220), lfo, gain: 3) * EnvGen.ar(Env.adsr(atk, dcy, 0, rls), doneAction: Done.freeSelf);

	Out.ar(out, x!2);
}).add;
)


a = Synth(\synth, [\out, 0, \atk, 1, \dcy, 0.5, \rls, 0.5, \cutoff, 5000, \lfoRate, 0.5]);


(
a = Synth(\synth, [\out, 0, \atk, 1, \dcy, 0.5, \rls, 0.5, \cutoff, 5000, \lfoRate, 0.5]);
s.record(duration: 3);
)
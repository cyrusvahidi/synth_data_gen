s.boot;

(
SynthDef(\synth, {
	|out=0, f0=220, atk=0.01, dcy=1, rls=1, cutoff=1000, mix=0, lfoRate=0.5|
	var osc1, osc2, lfo, env;

	lfo = SinOsc.kr(freq: lfoRate).range(0, 20000);
	env = EnvGen.ar(Env.new([0, 10000, 0], [atk, dcy, rls]));

	// choose filter modulation source via flag
	f_mod = if (lfo_mod, { lfo },{ env });

	osc1 = Pulse.ar(f0, mul: 1 - mix);
    osc2 = Saw.ar(f0, mix);

	x = MoogFF.ar(osc1 + osc2, f_mod, gain: 3) * EnvGen.ar(Env.adsr(atk, dcy, 0, rls), doneAction: Done.freeSelf);

	Out.ar(out, x!2);
}).add;
)

a = Synth(\synth, [\out, 0, \atk, 1, \dcy, 0.5, \rls, 0.5, \cutoff, 5000, \lfoRate, 0.5, \mix, 1]);


(
a = Synth(\synth, [\out, 0, \atk, 1, \dcy, 0.5, \rls, 0.5, \cutoff, 5000, \lfoRate, 0.5]);
s.record(duration: 3);
)
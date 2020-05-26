s.boot;

thisProcess.platform.recordingsDir = "/Users/cyrus/Documents/AIM/projects/data/"


(
SynthDef(\synth, {
	|out=0, f0=220, atk=0.01, dcy=1, rls=1, cutoff=1000, mix=0, lfo_rate=0.5, use_lfo=1,
	f_start=0, f_max=1000, f_end=0|
	var osc1, osc2, f_lfo, f_env, f_mod;

	// oscillators
	osc1 = Pulse.ar(f0, mul: 1 - mix);
    osc2 = Saw.ar(f0, mix);

	// filter modulation sources
	f_lfo = SinOsc.kr(freq: lfo_rate).range(0, 20000);
	f_env = EnvGen.ar(Env.new([f_start, f_max, f_end], [atk, dcy, rls]));
	// choose filter modulation source via flag
	f_mod = if (use_lfo, f_lfo, f_env);

	x = MoogFF.ar(osc1 + osc2, f_mod, gain: 3) * EnvGen.ar(Env.adsr(atk, dcy, 0, rls, curve: 4), doneAction: Done.freeSelf);

	Out.ar(out, x!2);
}).add;
)

(
var envs = List[List[0, 1, 1], List[0, 0.5, 1.5], List[0.5, 1, 0.5], List[1, 0.8, 0.2]];
var lfo_rates = List[0.5, 2, 4];
var filter_envs = List[List[0, 5000, 0], List[0, 10000, 0]];
var mixes = List[0, 0.5, 1];
var use_lfo = List[1, 0];

f = Routine({
	envs.do({
		arg env;
		mixes.do({
			arg mix;
			use_lfo.do({
				arg flag;
				if (flag == 1,
					{lfo_rates.do({
						arg rate;
						Synth(\synth, [
							\out, 0,
							\atk, env[0], \dcy, env[1], \rls, env[2],
							\lfo_rate, rate, \use_lfo, flag,
							\mix, mix
						]);
						2.05.wait;
					})},
					{filter_envs.do({
						arg f_env;
						Synth(\synth, [
							\out, 0,
							\atk, env[0], \dcy, env[1], \rls, env[2],
							\use_lfo, flag,
							\f_start, f_env[0], \f_max, f_env[1], \f_end, f_env[2],
							\mix, mix
						]);
						2.05.wait;
					})}
				);
			});
		});
	});
}).play;

)


// record ugen
a = Synth(\synth, [
	\out, 0,
	\atk, 1, \dcy, 0.5, \rls, 0.5,
	\cutoff, 5000, \lfoRate, 3, \use_lfo, 1,
	\mix, 0.5
]);
s.record(duration: 2);
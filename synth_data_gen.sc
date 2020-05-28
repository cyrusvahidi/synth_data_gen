s.boot;

thisProcess.platform.recordingsDir = "/Users/cyrus/Documents/AIM/projects/data/";

(
SynthDef(\synth, {
	|out=0, f0=440,
	atk=0.01, dcy=1, sustain=0, sustain_lvl=0.5, rls=1,
	cutoff=1000,
	mix=0,
	lfo_rate=0.5, use_lfo=1,
	f_start=0, f_max=1000, f_end=0|
	var osc1, osc2, f_lfo, f_env, f_mod, env;

	// oscillators
	osc1 = Pulse.ar(f0, mul: 1 - mix);
    osc2 = Saw.ar(f0, mix);

	// filter modulation sources
	f_lfo = SinOsc.kr(freq: lfo_rate).range(0, 20000);
	f_env = EnvGen.ar(Env.new([f_start, f_max, f_end], [atk, dcy, rls]));
	// choose filter modulation source via flag
	f_mod = if (use_lfo, f_lfo, f_env);

	env = EnvGen.ar(Env([0, 1, sustain_lvl, sustain_lvl, 0], [atk, dcy, sustain, rls], -4));
	// EnvGen.ar(Env.adsr(atk, dcy, 0, rls, curve: 2), doneAction: Done.freeSelf)
	x = MoogFF.ar(osc1 + osc2, f_mod, gain: 2) * env;

	Out.ar(out, x!2);
}).add;
)

(
var envs = List[List[0, 1, 1], List[0, 0.5, 1.5], List[0.5, 1, 0.5], List[1, 0.8, 0.2]];
var lfo_rates = List[0.5, 2];
var filter_envs = List[List[0, 5000, 0], List[0, 10000, 0]];
var mixes = List[0];
var use_lfo = List[1, 0];
var path;

f = Routine({
	envs.do({arg env;
			use_lfo.do({arg flag;
				if (flag == 1,
				{
					lfo_rates.do({arg rate;
						Synth(\synth, [
							\atk, env[0], \dcy, env[1], \rls, env[2],
							\lfo_rate, rate, \use_lfo, flag
						]);
						path = "atk_" ++ env[0] + "dcy_" ++ env[1] + "lfo_" ++ rate ++ '.wav';
						path = thisProcess.platform.recordingsDir + path;
						s.record(path: path, duration: 2);
						2.05.wait;
					})
				},
				{
					filter_envs.do({arg f_env;
						Synth(\synth, [
							\atk, env[0], \dcy, env[1], \rls, env[2],
							\use_lfo, flag,
							\f_start, f_env[0], \f_max, f_env[1], \f_end, f_env[2],
						]);
						path = "atk_" ++ env[0] + "dcy_" ++ env[1] + "fenv_s_" ++ f_env[0] + "fenv_e_" ++ f_env[1] ++ '.wav';
						path = thisProcess.platform.recordingsDir + path;
						s.record(path: path, duration: 2);
						2.05.wait;
					})
				}
				);
			});
	});
}).play;

)


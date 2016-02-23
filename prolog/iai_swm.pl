/** iai_swm


  Copyright (C) 2016 Fereshta Yazdani
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions are met:
      * Redistributions of source code must retain the above copyright
        notice, this list of conditions and the following disclaimer.
      * Redistributions in binary form must reproduce the above copyright
        notice, this list of conditions and the following disclaimer in the
        documentation and/or other materials provided with the distribution.
      * Neither the name of the <organization> nor the
        names of its contributors may be used to endorse or promote products
        derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

  @author Fereshta Yazdani
  @license BSD
*/

:- module(iai_swm,
  [
   swm_interface/0,
   swm_interface/1,
   swm_AgentsData/2,
   swm_EntitiesData/1
 ]).

:- use_module(library('semweb/rdf_db')).
:- use_module(library('semweb/rdfs')).
:- use_module(library('owl_parser')).
:- use_module(library('owl')).
:- use_module(library('rdfs_computable')).
:- use_module(library('knowrob_owl')).
:- use_module(library('jpl')).

:- rdf_meta swm_test(r,r),
    swm_interface(r),
    swm_AgentsData(r,r),
    swm_EntitiesData(r).

:- rdf_db:rdf_register_ns(knowrob, 'http://knowrob.org/kb/knowrob.owl#', [keep(true)]).

swm_interface :- swm_interface(_).

:- assert(swm_inter(fail)).
swm_interface(SWM) :-
    swm_inter(fail),
    jpl_new('com.github.iai_swm.SWMConnection', [], SWM),
    retract(swm_inter(fail)),
    assert(swm_inter(SWM)),!.
    
swm_interface(DB) :-
    swm_inter(DB). 
    
swm_AgentsData(Ant, Bnt):-
    swm_interface(SWM),
    format("AgentsData"),
    jpl_call(SWM, 'queryAgentsData',[Ant],Bnt).

swm_EntitiesData(Ant):-
    swm_interface(SWM),
    jpl_call(SWM, 'queryEntitiesData',[],Ant).

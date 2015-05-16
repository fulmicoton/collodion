actions = require './actions.coffee'
fulmicoton = require './fulmicoton.coffee'
api = require './api.coffee'




class CollodionStore extends fulmicoton.Store

	events: -> ["docChange", "corpusChange"]

	bind: (action, handler)->
		action.bind handler.bind(this)

	bindActions: ->
		@bind actions.selectDoc, @onSelectDoc
		@bind actions.reloadAnalyzer, @onReloadAnalyzer

	getNbDocs: ->
		@corpus.nbDocs

	init: ->
		@corpus = new api.Corpus()
		@analyzer = new api.Analyzer()
		@corpus.get =>
			@events.corpusChange.trigger()
		@onSelectDoc 0

	getDoc: ->
		@doc

	# -------	

	onReloadAnalyzer: ->
		@analyzer.refresh =>
			@onSelectDoc @getDoc().docId

	onSelectDoc: (docId)->
		@doc = @corpus.fetchDoc docId, (doc)=>
			@events.docChange.trigger()



module.exports = new CollodionStore()

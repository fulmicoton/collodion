actions = require './actions.coffee'
fulmicoton = require './fulmicoton.coffee'
api = require './api.coffee'


class CollodionStore extends fulmicoton.Store

	events: -> ["docChange", "corpusChange"]

	bindActions: ->
		actions.selectDoc.bind @onSelectDoc.bind(this)

	getNbDocs: ->
		@corpus.nbDocs

	init: ->
		@corpus = new api.Corpus()
		@corpus.get =>
			@events.corpusChange.trigger()
		@onSelectDoc 0

	getDoc: ->
		@doc

	# -------	

	onSelectDoc: (docId)->
		@doc = @corpus.fetchDoc docId, (doc)=>
			@events.docChange.trigger()



module.exports = new CollodionStore()
